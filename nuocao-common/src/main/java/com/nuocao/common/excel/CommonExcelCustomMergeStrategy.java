package com.nuocao.common.excel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 纵向合并策略：把指定列中「连续相同」的单元格合并为一块（常用于按分组字段合并）。
 *
 * <p>相比旧实现，主要改进：</p>
 * <ul>
 *   <li><b>合并时机</b>：改为在 {@link SheetWriteHandler#afterSheetCreate} 中执行，此时整表已写完，
 *       行号完全确定，不再依赖「第一个 merge 回调落在哪一行」来定位数据起始行；</li>
 *   <li><b>自动适配多级表头</b>：由「数据行数」反推首个数据行
 *       （{@code firstDataRow = lastRowNum - dataSize + 1}），三级表头、单级表头都无需额外配置；</li>
 *   <li><b>无状态</b>：旧实现的 {@code rowIndex} 是可变字段，同一个策略实例被复用或并发时会有问题，
 *       现改为不可变字段；</li>
 *   <li><b>空值安全</b>：分组比较改用 {@link Objects#equals}，避免旧实现 {@code value.equals()} 的 NPE；</li>
 *   <li><b>多列支持</b>：可一次合并多列；合并区域做去重且使用带校验的 {@code addMergedRegion}。</li>
 * </ul>
 *
 * <p>用法（值按数据行顺序传入，仅用于决定分组，即「每几行合并一次」）：</p>
 * <pre>
 * .registerWriteHandler(new CommonExcelCustomMergeStrategy(orgNameList, 0))        // 合并第 0 列
 * .registerWriteHandler(new CommonExcelCustomMergeStrategy(orgNameList, new int[]{0, 9})) // 合并第 0、9 列
 * </pre>
 */
public class CommonExcelCustomMergeStrategy implements SheetWriteHandler {

    /** 待合并目标列的取值（按数据行顺序），仅用于判断连续相同分组 */
    private final List<String> exportDataList;

    /** 需要合并的列下标（0 基） */
    private final int[] targetColumnIndexes;

    /**
     * @param exportDataList    目标列的取值（按数据行顺序）
     * @param targetColumnIndex 目标列下标，为空时默认第 0 列
     */
    public CommonExcelCustomMergeStrategy(List<String> exportDataList, Integer targetColumnIndex) {
        this(exportDataList, targetColumnIndex == null ? new int[]{0} : new int[]{targetColumnIndex});
    }

    /**
     * 多列合并。
     *
     * @param exportDataList     目标列的取值（按数据行顺序）
     * @param targetColumnIndexes 目标列下标数组
     */
    public CommonExcelCustomMergeStrategy(List<String> exportDataList, int[] targetColumnIndexes) {
        this.exportDataList = exportDataList == null ? Collections.emptyList() : exportDataList;
        this.targetColumnIndexes = (targetColumnIndexes == null || targetColumnIndexes.length == 0)
                ? new int[]{0}
                : targetColumnIndexes.clone();
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder workbookHolder, WriteSheetHolder sheetHolder) {
        Sheet sheet = sheetHolder.getSheet();
        int dataSize = exportDataList.size();
        if (sheet == null || dataSize == 0) {
            return;
        }
        // 由数据行数反推首个数据行下标，自动跳过任意层级的表头
        int firstDataRow = sheet.getLastRowNum() - dataSize + 1;
        if (firstDataRow < 0) {
            return;
        }
        for (int columnIndex : targetColumnIndexes) {
            mergeColumn(sheet, columnIndex, firstDataRow, dataSize);
        }
    }

    /**
     * 将某一列中连续相同的行合并为一块。
     */
    private void mergeColumn(Sheet sheet, int columnIndex, int firstDataRow, int dataSize) {
        int groupStart = 0;
        for (int i = 0; i < dataSize; i++) {
            boolean groupEnd = (i == dataSize - 1) || !Objects.equals(exportDataList.get(i), exportDataList.get(i + 1));
            if (groupEnd) {
                if (i > groupStart) {
                    addMergedRegion(sheet, firstDataRow + groupStart, firstDataRow + i, columnIndex);
                }
                groupStart = i + 1;
            }
        }
    }

    private void addMergedRegion(Sheet sheet, int firstRow, int lastRow, int columnIndex) {
        CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, columnIndex, columnIndex);
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress exist = sheet.getMergedRegion(i);
            if (exist.getFirstRow() == region.getFirstRow()
                    && exist.getLastRow() == region.getLastRow()
                    && exist.getFirstColumn() == region.getFirstColumn()
                    && exist.getLastColumn() == region.getLastColumn()) {
                return;
            }
        }
        sheet.addMergedRegion(region);
    }
}
