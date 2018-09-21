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
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import application.MySqlConnection;
import application.SelfInfo;

public class CreateDeliverynotePdf {

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font mediumFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);
	private static Font mediumFontBold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
	
	private static SelfInfo self = new SelfInfo();
	private static String companyName = self.getCompany();
	private static String companyAddress = self.getCompanyAddress();
	private static String companyEmail = self.getCompanyEmail();

	private static String deliverynoteNumber;
	private static String[] partnerInfo = new String[3];
	private static String license1;
	private static String license2;

	public static void createDeliverynotePdf(String input, String l1, String l2) throws FileNotFoundException, DocumentException {

		deliverynoteNumber = input;
		input = input.replaceAll("\\/", "-");
		partnerInfo = getPartnerInfo();
		license1 = l1;
		license2 = l2;

		try {
			Document document = new Document(PageSize.A4);

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("deliverynote_" + input + ".pdf"));
			document.open();

			addMetaData(document);
			addHeader(document);
			createTable(document);

			addFooter(document, writer);
			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			partnerInfo[0] = partnerInfo[1] = partnerInfo[2] = "";
			deliverynoteNumber = "";
		}

	}

	private static void addMetaData(Document document) {
		document.addTitle("RIMAR - Szállítólevél");
		document.addSubject("RIMAR Raktári Információs rendszer - Szakdolgozat 2018 EKE");
		document.addKeywords("Szakdolgozat, Java, iText");
		document.addAuthor("Pálosi Péter");
		document.addCreator("Pálosi Péter");
	}

	private static void addHeader(Document document) throws DocumentException, IOException {

		Paragraph empty1Line = new Paragraph();

		addEmptyLine(empty1Line, 1);

		document.add(empty1Line);

		Paragraph prghTitle = new Paragraph("Szállítólevél", catFont);
		prghTitle.setAlignment(Element.ALIGN_CENTER);
		
		document.add(prghTitle);
		
		Paragraph prghDeliverynoteNumber = new Paragraph(deliverynoteNumber, mediumFont);
		prghDeliverynoteNumber.setAlignment(Element.ALIGN_CENTER);

		document.add(prghDeliverynoteNumber);
		
		document.add(empty1Line);
		
		
		Font spclFont = new Font(tryCreateBaseFont(), 12, Font.NORMAL);
		
		
		Chunk glue1 = new Chunk(new VerticalPositionMark());
		Paragraph p1 = new Paragraph("Feladó", mediumFontBold);
		p1.add(new Chunk(glue1));
		p1.add("Címzett");
		document.add(p1);

		Chunk glue2 = new Chunk(new VerticalPositionMark());
		Paragraph p2 = new Paragraph(companyName, smallFont);
		p2.add(new Chunk(glue2));
		p2.add(partnerInfo[0]);
		document.add(p2);

		Chunk glue3 = new Chunk(new VerticalPositionMark());
		Paragraph p3 = new Paragraph(companyAddress, smallFont);
		p3.add(new Chunk(glue3));
		p3.add(partnerInfo[1]);
		document.add(p3);

		Chunk glue4 = new Chunk(new VerticalPositionMark());
		Paragraph p4 = new Paragraph(companyEmail, smallFont);
		p4.add(new Chunk(glue4));
		p4.add(partnerInfo[2]);
		document.add(p4);

		document.add(empty1Line);
		document.add(empty1Line);
		
		Paragraph prghDate = new Paragraph("Kiállítás dátuma: " + getCreationDate(), smallFont);
		prghDate.setAlignment(Element.ALIGN_LEFT);
		
		document.add(prghDate);
		
		Paragraph prghCreator = new Paragraph("Kiállító: " + getCreator(), spclFont);
		prghCreator.setAlignment(Element.ALIGN_LEFT);
		
		document.add(prghCreator);
		
		Paragraph prghLicense1 = new Paragraph("Teherautó rendszáma: " + license1, smallFont);
		Paragraph prghLicense2 = new Paragraph("Pótkocsi rendszáma: " + license2, smallFont);
		
		document.add(prghLicense1);
		document.add(prghLicense2);
			
		document.add(empty1Line);
		document.add(empty1Line);
		
		
	}
	
	private static void createTable(Document document) throws DocumentException {
		
		PdfPTable table = new PdfPTable(3);

		table.setTotalWidth(PageSize.A4.getWidth() - 50);
		table.setLockedWidth(true);

		

		PdfPCell c1 = new PdfPCell(new Phrase("Sorszám", smallBold));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Cikkszám", smallBold));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Mennyiség", smallBold));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBorder(Rectangle.BOTTOM);
		c1.setPadding(10);
		table.addCell(c1);
		
		
		table.setHeaderRows(1);

		fillTableData(table);

		document.add(table);
		
	}
	
	private static void fillTableData(PdfPTable table) {
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "SELECT part_number, pallet_quantity FROM deliverynote WHERE deliverynote_number = ?";
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			
			ps.setString(1, deliverynoteNumber);
			
			ResultSet rs = ps.executeQuery();
			
			int i = 1;
			
			while(rs.next()) {
				
				PdfPCell c1 = new PdfPCell(new Phrase(i + "", smallFont));
				c1.setBorder(0);
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c2 = new PdfPCell(new Phrase(rs.getString(1), smallFont));
				c2.setBorder(0);
				c2.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c3 = new PdfPCell(new Phrase(rs.getDouble(2) + "", smallFont));
				c3.setBorder(0);
				c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				
				i++;
				
				table.addCell(c1);
				table.addCell(c2);
				table.addCell(c3);
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			myConnection.disconnect();
		}
		
	}
	
	private static void addFooter(Document document, PdfWriter writer) throws DocumentException, IOException {
		
		PdfContentByte cb = writer.getDirectContent();

		BaseFont bf_times_bold = BaseFont.createFont(BaseFont.TIMES_BOLD, "Cp1252", false);
		
		
		
		
		cb.beginText();
		cb.setFontAndSize(bf_times_bold, 12);
		cb.setTextMatrix(100, 110);
		cb.showText("Kiállító");
		cb.endText();

		cb.beginText();
		cb.setFontAndSize(bf_times_bold, 12);
		cb.setTextMatrix(460, 110);
		cb.showText("Címzett");
		cb.endText();

		cb.moveTo(60, 120);
		cb.lineTo(179, 120);
		cb.stroke();

		cb.moveTo(420, 120);
		cb.lineTo(535, 120);
		cb.stroke();
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private static int getPartnerId() {

		int id = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_id from deliverynote WHERE deliverynote_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, deliverynoteNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				id = rs.getInt(1);

			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {

			myConnection.disconnect();
		}

		return id;

	}

	private static String[] getPartnerInfo() {

		int id = getPartnerId();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_name, partner_address, partner_email FROM partners WHERE partner_id = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				partnerInfo[0] = rs.getString(1);
				partnerInfo[1] = rs.getString(2);
				partnerInfo[2] = rs.getString(3);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return partnerInfo;
	}

	private static String getCreationDate() {

		String creationDate = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT creation_date from deliverynote WHERE deliverynote_number = ? LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, deliverynoteNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				creationDate = rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		int length = creationDate.length();

		creationDate = creationDate.substring(0, length - 5);

		return creationDate;
	}
	
	
	private static String getCreator() {

		String creator = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT user_name FROM users, deliverynote WHERE deliverynote_number = ? AND deliverynote.user_id = users.user_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, deliverynoteNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				creator = rs.getString(1);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return creator;

	}
	
	private static BaseFont tryCreateBaseFont() throws DocumentException, IOException {
		BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.EMBEDDED);
		return baseFont;
	}
	
}
