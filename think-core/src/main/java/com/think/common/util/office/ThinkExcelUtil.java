package com.think.common.util.office;

import com.think.common.util.FileUtil;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @Date :2021/6/25
 * @Name :ThinkExcelUtil
 * @Description : 请输入
 */
public class ThinkExcelUtil {

    private static   String LOCAL_PATH ="c://";// new ApplicationHome(ThinkExcelUtil.class).getSource().getParent()+File.separator+"upload"+File.separator"";


    public static void main(String[] args) throws ParserConfigurationException, TransformerException, IOException {

        Long x = -91L ;
        System.out.println(x.toBinaryString(-91));
        System.out.println(Short.MAX_VALUE);
        System.out.println(Short.MIN_VALUE);

        String s = excelToHtml("D:\\oneDrive\\软件部产品推进和设计文件夹\\我部重要文档方案等\\部门制度\\版本管理办法\\试行内容1.81版本\\x.xlsx");


    }

    /**通过属性利于重复利用HSSFCellStyle*/
    private static HashMap<Integer, HSSFCellStyle> styleMap = new HashMap();
    /**
     * 转换excel为html
     * @param path 原文件路径
     * @return 新文件路径
     * @throws TransformerException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static String excelToHtml(String path) throws TransformerException, IOException, ParserConfigurationException {
        InputStream inputStream = new FileInputStream(path);
        HSSFWorkbook excelBook= new HSSFWorkbook();
        try(Workbook workbook = WorkbookFactory.create(inputStream)){
            if (workbook instanceof XSSFWorkbook) {
                transformXSSF((XSSFWorkbook) workbook, excelBook);
            }else {
                excelBook = (HSSFWorkbook)workbook;
            }
        } catch (Exception e) {
            throw new RuntimeException("excel解析失败:"+e.getMessage(),e);
        }
        return getString(excelBook);
    }
    /**
     * 将xlsx文件转为xls
     * @param workbookOld 原xls
     * @param workbookNew 新的xlsx
     */
    public static void transformXSSF(XSSFWorkbook workbookOld, HSSFWorkbook workbookNew) {
        HSSFSheet sheetNew;
        XSSFSheet sheetOld;

        workbookNew.setMissingCellPolicy(workbookOld.getMissingCellPolicy());

        for (int i = 0; i < workbookOld.getNumberOfSheets(); i++) {
            sheetOld = workbookOld.getSheetAt(i);
            sheetNew = workbookNew.createSheet(sheetOld.getSheetName());
            transform(workbookOld, workbookNew, sheetOld, sheetNew);
        }
    }

    private static String getString(HSSFWorkbook excelBook) throws ParserConfigurationException, TransformerException, IOException {
        //生成html在本地的保存地址
        String targetFileName = LOCAL_PATH +  "html" + File.separator;
        //生成html名称
        String htmlName = System.currentTimeMillis() + ".html";
        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        //去掉Excel头行
        excelToHtmlConverter.setOutputColumnHeaders(false);
        //去掉Excel行号
        excelToHtmlConverter.setOutputRowNumbers(true);

        excelToHtmlConverter.processWorkbook(excelBook);

        excelToHtmlConverter.setUseDivsToSpan(true);


        Document htmlDocument = excelToHtmlConverter.getDocument();

        String content;
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(outStream);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();


            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");

            serializer.transform(domSource, streamResult);
            outStream.close();

            //Excel转换成Html
            content = new String(outStream.toByteArray());
        }
        //获取h2中值，就是sheet的name
        System.out.println(content);
        List<String> h2 = getH2(content);
        //去掉h2标签
        String s1 = content.replaceAll("<h2.+?</h2>", "");
        //拼接样式
        String s2 = getStyle(s1);
        //拼接表头
        String s3 = getHead(s2, h2);
        //拼接js
        String s4 = getJs(s3,h2);
        FileUtil.write(targetFileName +"" + htmlName,content );
//        FileUtils.writeStringToFile(new File(targetFileName, htmlName), content, "utf-8");

        return targetFileName + htmlName;
    }


    //拼接样式
    public static String getStyle(String content) {
        String regex = "</style>";
        String newStyle = "    .displayNone {\n" +
                "      display: none;\n" +
                "    }\n" +
                "\n" +
                "    .backGround {\n" +
                "      border: 1px solid;\n" +
                "      border-bottom: 0px;\n" +
                "      display: inline-block;\n" +
                "      min-width: 80px;\n" +
                "      height: 30px;\n" +
                "      font-weight: bolder;\n" +
                "      line-height: 30px;\n" +
                "      text-align: center;\n" +
                "      background-color: #eeeeee;\n" +
                "    }\n" +
                "\n" +
                "    .noBackGround {\n" +
                "      border: 1px solid;\n" +
                "      border-bottom: 0px;\n" +
                "      display: inline-block;\n" +
                "      min-width: 80px;\n" +
                "      height: 30px;\n" +
                "      font-weight: bolder;\n" +
                "      line-height: 30px;\n" +
                "      text-align: center;\n" +
                "      background-color: white;\n" +
                "    }\n" +
                "  </style>\n";
        return content.replace(regex, newStyle);
    }

    //拼接表头
    public static String getHead(String content, List<String> h2) {
        String regex = "<body class=\"b1\">";
        StringBuilder sb = new StringBuilder(regex + "\n");
        for (int i = 0; i < h2.size(); i++) {
            if (i == 0) {
                sb.append("<span class=\"backGround\" onclick=\"change(" + i +")\">" + h2.get(i) +"</span>\n");
            }else {
                sb.append("<span class=\"noBackGround\" onclick=\"change(" + i +")\">" + h2.get(i) +"</span>\n");
            }
        }
        return content.replace(regex, sb.toString());
    }

    //拼接js代码
    private static String getJs(String content, List<String> h2) {
        String regex = "</body>";
        String t1 = "</body>\n" +
                "<script>\n" +
                "  function change(flag) {\n" +
                "    var tbody = document.querySelectorAll('tbody')\n" +
                "    var spans = document.querySelectorAll('span')\n" +
                "    switch (flag) {\n";
        String t3 = "    }\n" +
                "  }\n" +
                "  change(0)\n" +
                "</script>\n";
        StringBuilder t2 = new StringBuilder("");
        t2.append(t1);
        for (int i = 0; i < h2.size(); i++) {
            t2.append("case " + i + ":\n");
            for (int j = 0; j < h2.size(); j++) {
                if (i == j) {
                    t2.append("tbody[" + j +"].className = 'displayBlock'\n");
                    t2.append("spans[" + j +"].className = 'backGround'\n");
                }else {
                    t2.append("tbody[" + j +"].className = 'displayNone'\n");
                    t2.append("spans[" + j +"].className = 'noBackGround'\n");
                }
            }
            t2.append("break;\n");
        }
        t2.append(t3);
        return content.replace(regex, t2.toString());
    }

    //提取h2标签
    private static List<String> getH2(String content) {
        String regex = "<h2>(.*?)</h2>";
        List<String> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }


    private static void transform(XSSFWorkbook workbookOld, HSSFWorkbook workbookNew,
                                  XSSFSheet sheetOld, HSSFSheet sheetNew) {

        sheetNew.setDisplayFormulas(sheetOld.isDisplayFormulas());
        sheetNew.setDisplayGridlines(sheetOld.isDisplayGridlines());
        sheetNew.setDisplayGuts(sheetOld.getDisplayGuts());
        sheetNew.setDisplayRowColHeadings(sheetOld.isDisplayRowColHeadings());
        sheetNew.setDisplayZeros(sheetOld.isDisplayZeros());
        sheetNew.setFitToPage(sheetOld.getFitToPage());

        sheetNew.setHorizontallyCenter(sheetOld.getHorizontallyCenter());
        sheetNew.setMargin(Sheet.BottomMargin,
                sheetOld.getMargin(Sheet.BottomMargin));
        sheetNew.setMargin(Sheet.FooterMargin,
                sheetOld.getMargin(Sheet.FooterMargin));
        sheetNew.setMargin(Sheet.HeaderMargin,
                sheetOld.getMargin(Sheet.HeaderMargin));
        sheetNew.setMargin(Sheet.LeftMargin,
                sheetOld.getMargin(Sheet.LeftMargin));
        sheetNew.setMargin(Sheet.RightMargin,
                sheetOld.getMargin(Sheet.RightMargin));
        sheetNew.setMargin(Sheet.TopMargin, sheetOld.getMargin(Sheet.TopMargin));
        sheetNew.setPrintGridlines(sheetNew.isPrintGridlines());
        sheetNew.setRightToLeft(sheetNew.isRightToLeft());
        sheetNew.setRowSumsBelow(sheetNew.getRowSumsBelow());
        sheetNew.setRowSumsRight(sheetNew.getRowSumsRight());
        sheetNew.setVerticallyCenter(sheetOld.getVerticallyCenter());


        HSSFRow rowNew;
        int lastColumn = 0;
        for (Row row : sheetOld) {
            rowNew = sheetNew.createRow(row.getRowNum());
            lastColumn = transform(lastColumn,workbookOld, workbookNew, (XSSFRow) row, rowNew);

        }

        for (int i = 0; i < lastColumn; i++) {
            sheetNew.setColumnWidth(i, sheetOld.getColumnWidth(i));
            sheetNew.setColumnHidden(i, sheetOld.isColumnHidden(i));
        }

        for (int i = 0; i < sheetOld.getNumMergedRegions(); i++) {
            CellRangeAddress merged = sheetOld.getMergedRegion(i);
            sheetNew.addMergedRegion(merged);
        }

     }

    private static int transform(int lastColumn,XSSFWorkbook workbookOld, HSSFWorkbook workbookNew,
                                 XSSFRow rowOld, HSSFRow rowNew) {
        HSSFCell cellNew;
        rowNew.setHeight(rowOld.getHeight());

        for (Cell cell : rowOld) {
            cellNew = rowNew.createCell(cell.getColumnIndex());

//            HSSFCellStyle cellStyle = workbookNew.createCellStyle();
//            cellStyle.setFillBackgroundColor((short)1);

//            cellNew.setCellStyle(          cellStyle );
//
            //cellNew.setCellStyle(cell.getCellStyle());
            //System.out.println(cellNew.toString() + " >?" + cellNew.getCellStyle().getFillBackgroundColor());
            transform(workbookOld, workbookNew, (XSSFCell) cell, cellNew);
        }
        return Math.max(lastColumn, rowOld.getLastCellNum());
    }

    private static void transform(XSSFWorkbook workbookOld, HSSFWorkbook workbookNew,
                                  XSSFCell cellOld, HSSFCell cellNew) {
        cellNew.setCellComment(cellOld.getCellComment());

        Integer hash = cellOld.getCellStyle().hashCode();
        if (!styleMap.containsKey(hash)) {
            transform(workbookOld, workbookNew, hash,
                    cellOld.getCellStyle(),
                    workbookNew.createCellStyle());
        }
        cellNew.setCellStyle(styleMap.get(hash));

        switch (cellOld.getCellType()) {
            case BLANK:
                break;
            case BOOLEAN:
                cellNew.setCellValue(cellOld.getBooleanCellValue());
                break;
            case ERROR:
                cellNew.setCellValue(cellOld.getErrorCellValue());
                break;
            case FORMULA:
                cellNew.setCellValue(cellOld.getCellFormula());
                break;
            case NUMERIC:
                cellNew.setCellValue(cellOld.getNumericCellValue());
                break;
            case STRING:
                cellNew.setCellValue(cellOld.getStringCellValue());
                break;
            default:
                System.out.println("transform: Unbekannter Zellentyp "
                        + cellOld.getCellType());
        }
    }

    private static void transform(XSSFWorkbook workbookOld, HSSFWorkbook workbookNew,
                                  Integer hash, XSSFCellStyle styleOld, HSSFCellStyle styleNew) {
        styleNew.setAlignment(styleOld.getAlignmentEnum());
        styleNew.setVerticalAlignment(styleOld.getVerticalAlignmentEnum());
        styleNew.setBorderBottom(styleOld.getBorderBottomEnum());
        styleNew.setBorderLeft(styleOld.getBorderLeftEnum());
        styleNew.setBorderRight(styleOld.getBorderRightEnum());
        styleNew.setBorderTop(styleOld.getBorderTopEnum());
        styleNew.setTopBorderColor(styleOld.getTopBorderColor());
        styleNew.setBottomBorderColor(styleOld.getTopBorderColor());
        styleNew.setLeftBorderColor(styleOld.getLeftBorderColor());
        styleNew.setRightBorderColor(styleOld.getRightBorderColor());
        styleNew.setDataFormat(transform(workbookOld, workbookNew,
                styleOld.getDataFormat()));
        styleNew.setFillBackgroundColor(styleOld.getFillBackgroundColor());
        styleNew.setFillForegroundColor(styleOld.getFillForegroundColor());
        styleNew.setFillPattern(styleOld.getFillPatternEnum());
        styleNew.setFont(transform(workbookNew,
                styleOld.getFont()));
        styleNew.setHidden(styleOld.getHidden());
        styleNew.setIndention(styleOld.getIndention());
        styleNew.setLocked(styleOld.getLocked());
        styleNew.setWrapText(styleOld.getWrapText());
        styleMap.put(hash, styleNew);
    }

    private static short transform(XSSFWorkbook workbookOld, HSSFWorkbook workbookNew,
                                   short index) {
        DataFormat formatOld = workbookOld.createDataFormat();
        DataFormat formatNew = workbookNew.createDataFormat();
        return formatNew.getFormat(formatOld.getFormat(index));
    }

    private static HSSFFont transform(HSSFWorkbook workbookNew, XSSFFont fontOld) {
        HSSFFont fontNew = workbookNew.createFont();
        fontNew.setBold(fontOld.getBold());
        fontNew.setCharSet(fontOld.getCharSet());
        fontNew.setColor(fontOld.getColor());
        fontNew.setFontName(fontOld.getFontName());
        fontNew.setFontHeight(fontOld.getFontHeight());
        fontNew.setItalic(fontOld.getItalic());
        fontNew.setStrikeout(fontOld.getStrikeout());
        fontNew.setTypeOffset(fontOld.getTypeOffset());
        fontNew.setUnderline(fontOld.getUnderline());
        return fontNew;
    }

}
