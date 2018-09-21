/*******************************************************************************
 * Szakdolgozat
 * Raktári információs rendszer fejlesztése Java nyelven
 *
 * Eszterházy Károly Egyetem
 * Alkalmazott Informatika Tanszék
 *
 * Készítette: Pálosi Péter gazdaságinformatikus BSc levelező tagozat
 *
 * Gyöngyös, 2018
 *******************************************************************************/

package application.administrator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import application.LoginController;
import application.MySqlConnection;


public class CreateStockListPdf {

	
	private static Font bigFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font mediumFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

	private static String partNumber;

	@SuppressWarnings("unused")
	public static void createStockListPdf(String partNo) throws FileNotFoundException, DocumentException {

		partNumber = partNo;

		try {
			Document document = new Document(PageSize.A4);

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("stocklist_" + partNumber + "_.pdf"));
			document.open();

			addMetaData(document);
			addHeader(document);
			createTable(document);

			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			partNumber = "";
		}

	}

	private static void addMetaData(Document document) {
		document.addTitle("RIMAR - Készlet lista: " + partNumber);
		document.addSubject("RIMAR Raktári Információs rendszer - Szakdolgozat 2018 EKE");
		document.addKeywords("Szakdolgozat, Java, iText");
		document.addAuthor("Pálosi Péter");
		document.addCreator("Pálosi Péter");
	}

	private static void addHeader(Document document) throws DocumentException {

		Paragraph empty1Line = new Paragraph();

		addEmptyLine(empty1Line, 1);

		document.add(empty1Line);

		Paragraph prghTitle = new Paragraph("Készlet lista", bigFont);
		prghTitle.setAlignment(Element.ALIGN_CENTER);

		document.add(prghTitle);

		Paragraph prghPartner = new Paragraph(getPartnerName(partNumber), mediumFont);
		prghPartner.setAlignment(Element.ALIGN_CENTER);

		document.add(prghPartner);

		document.add(empty1Line);

		Paragraph prghPartNo = new Paragraph("Cikkszám: " + partNumber, smallFont);
		Paragraph prghUser = new Paragraph("Létrehozó: " + LoginController.getUsername(), smallFont);
		Paragraph prghTime = new Paragraph("Létrehozás időpontja: " + LocalDateTime.now().toString(), smallFont);

		document.add(prghPartNo);
		document.add(prghUser);
		document.add(prghTime);

		addEmptyLine(empty1Line, 2);

		document.add(empty1Line);

	}

	private static void createTable(Document document) throws DocumentException {

		PdfPTable table = new PdfPTable(5);
		table.setTotalWidth(PageSize.A4.getWidth() - 50);
		table.setLockedWidth(true);

		table.getDefaultCell().setBorder(0);

		PdfPCell c1 = new PdfPCell(new Phrase("Raklapazonosító", smallBold));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);

		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Pozíció", smallBold));
		
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Mennyiség", smallBold));
		
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Beérkezés dátuma", smallBold));
		
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Zárolt", smallBold));
		
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		table.setHeaderRows(1);

		fillTableData(table);

		document.add(table);
	}

	private static void fillTableData(PdfPTable table) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id, position_name, pallet_quantity, arrival_date, quarantine FROM pallet, positions WHERE pallet.pallet_position = positions.position_id AND part_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, partNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				int quarantine = rs.getInt(5);
				

				PdfPCell c1 = new PdfPCell(new Phrase(rs.getString(1), smallFont));
				c1.setBorder(0);
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c2 = new PdfPCell(new Phrase(rs.getString(2), smallFont));
				c2.setBorder(0);
				c2.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c3 = new PdfPCell(new Phrase(rs.getDouble(3) + "", smallFont));
				c3.setBorder(0);
				c3.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c4 = new PdfPCell(new Phrase(rs.getString(4), smallFont));
				c4.setBorder(0);
				c4.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				PdfPCell c5 = new PdfPCell(new Phrase(rs.getInt(5) + "", smallFont));
				c5.setBorder(0);
				c5.setHorizontalAlignment(Element.ALIGN_CENTER);

				table.addCell(c1);
				table.addCell(c2);
				table.addCell(c3);
				table.addCell(c4);
				if(quarantine == 1) {
					c5.setBackgroundColor(BaseColor.RED);
					
				}
				
				table.addCell(c5);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
			smallFont.setColor(BaseColor.BLACK);
		}

	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private static String getPartnerName(String partNumber) {

		String partnerName = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_name FROM partners, pallet WHERE part_number = ? AND pallet.partner_id = partners.partner_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, partNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				partnerName = rs.getString(1);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return partnerName;
	}

}
