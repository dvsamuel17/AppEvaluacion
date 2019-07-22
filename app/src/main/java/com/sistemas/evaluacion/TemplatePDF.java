package com.sistemas.evaluacion;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class TemplatePDF {
    private Context context;
    public File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle=new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle=new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText=new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHighText=new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);

    public TemplatePDF(Context context) {
        this.context = context;
    }

    public void openDocument(){
        createFile();
        try{
            document=new Document(PageSize.LETTER);
            pdfWriter=PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        }catch (Exception e){
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile(){
        File folder=new File("/storage/sdcard0/PDF");

        if(!folder.exists())
            folder.mkdir();
        pdfFile=new File(folder, "TemplatePDF.pdf");
        pdfFile.getAbsolutePath();
    }

    public void closeDocument(){
        document.close();
    }

    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subTitle, String date){
        try{
            paragraph=new Paragraph();
            addChild(new Paragraph(title, fTitle));
            addChild(new Paragraph(subTitle, fSubTitle));
            addChild(new Paragraph("Generado  "+date, fHighText));
            paragraph.setSpacingBefore(50);
            paragraph.setSpacingAfter(10);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("openDocument", e.toString());
        }
    }

    private void addChild(Paragraph childParagraph){
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text){
        try{
            paragraph=new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("openDocument", e.toString());
        }
    }

    public void createTable(String [] header, ArrayList<String[]> imputado){
        try{
            paragraph=new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable=new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(10);
            PdfPCell pdfPCell;
            int indexC=0;
            while (indexC<header.length){
                pdfPCell=new PdfPCell(new Phrase(header[indexC++], fSubTitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(BaseColor.GRAY);
                pdfPTable.addCell(pdfPCell);
            }

            for (int indexR=0; indexR<imputado.size();indexR++){
                String[] row=imputado.get(indexR);
                for (indexC=0; indexC<header.length;indexC++){
                    pdfPCell=new PdfPCell(new Phrase(row [indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    if (indexC==2){
                        switch (row[indexC]){
                            case "Bajo":
                                pdfPCell.setBackgroundColor(BaseColor.GREEN);
                                break;
                            case "Moderado":
                                pdfPCell.setBackgroundColor(BaseColor.YELLOW);
                                break;
                            case "Alto":
                                pdfPCell.setBackgroundColor(BaseColor.RED);
                                break;
                        }
                    }
                    pdfPTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfPTable);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("openDocument", e.toString());
        }

    }

    public void addImgName (String imageName) {
        try{
            Image image = Image.getInstance("/storage/sdcard0/Images" + File.separator + imageName);
            image.scaleAbsolute(PageSize.LETTER);
            image.setAbsolutePosition(0,0);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);
        }catch (Exception e){
            Log.e("addImgName ", e.toString());
        }
    }

    /*public void viewPDF(){
        Intent intent=new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }*/

    public void appViewPDF(Activity activity){
        if(pdfFile.exists()){
            Uri uri=Uri.fromFile(pdfFile);
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            try{
                activity.startActivity(intent);
            }catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Toast.makeText(activity.getApplicationContext(), "No cuentas con una aplicación para ver PDF", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(activity.getApplicationContext(), "El archivo no se encontro", Toast.LENGTH_LONG).show();
        }
    }
}
