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
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import application.MySqlConnection;
import application.SelfInfo;

public class CreateOffLoadListPdf extends PdfPageEventHelper {

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font mediumFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

	private static SelfInfo self = new SelfInfo();
	private static String companyName = self.getCompany();
	private static String companyAddress = self.getCompanyAddress();
	private static String companyPhone = self.getCompanyPhone();
	private static String companyEmail = self.getCompanyEmail();

	private static int offloadListTaskNum;

	public static void createPdf(int offloadListTaskNumber) throws FileNotFoundException, DocumentException {

		offloadListTaskNum = offloadListTaskNumber;

		try {
			Document document = new Document(PageSize.A4);

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("offloadlist_" + offloadListTaskNumber + "_.pdf"));
			document.open();

			addMetaData(document);
			addHeader(document);
			createTable(document);

			addFooter(document, writer);
			document.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			offloadListTaskNum = 0;
		}

	}

	private static void addMetaData(Document document) {
		document.addTitle("RIMAR - Kitárolási Lista");
		document.addSubject("RIMAR Raktári Információs rendszer - Szakdolgozat 2018 EKE");
		document.addKeywords("Szakdolgozat, Java, iText");
		document.addAuthor("Pálosi Péter");
		document.addCreator("Pálosi Péter");
	}

	private static void addHeader(Document document) throws DocumentException, IOException {
		Paragraph empty1Line = new Paragraph();

		addEmptyLine(empty1Line, 1);

		document.add(empty1Line);

		Paragraph prghCompany = new Paragraph(companyName, smallBold);

		document.add(prghCompany);

		Chunk glue1 = new Chunk(new VerticalPositionMark());
		Paragraph p1 = new Paragraph(companyAddress, smallFont);
		p1.add(new Chunk(glue1));
		p1.add("Feladatszám: " + offloadListTaskNum);
		document.add(p1);

		Chunk glue2 = new Chunk(new VerticalPositionMark());
		Paragraph p2 = new Paragraph("Tel.:" + companyPhone, smallFont);
		p2.add(new Chunk(glue2));
		p2.add("Létrehozás dátuma: " + getCreationDate());
		document.add(p2);

		Chunk glue3 = new Chunk(new VerticalPositionMark());
		Paragraph p3 = new Paragraph("E-mail: " + companyEmail, smallFont);
		p3.add(new Chunk(glue3));
		p3.add("Megjegyzés: " + getComments());
		document.add(p3);

		Font spclFont = new Font(tryCreateBaseFont(), 12, Font.NORMAL);

		Paragraph prghCreator = new Paragraph("Létrehozó: " + getCreator(), spclFont);
		prghCreator.setAlignment(Element.ALIGN_RIGHT);
		document.add(prghCreator);

		document.add(empty1Line);

		Paragraph prghMainTitle = new Paragraph("KITÁROLÁSI LISTA", catFont);
		prghMainTitle.setAlignment(Element.ALIGN_CENTER);
		document.add(prghMainTitle);

		Paragraph prghOffloadListNumber = new Paragraph(getOffloadListNumber(), mediumFont);
		prghOffloadListNumber.setAlignment(Element.ALIGN_CENTER);
		document.add(prghOffloadListNumber);

		document.add(empty1Line);

		Paragraph prghPartner = new Paragraph("Partner:", smallBold);
		prghPartner.setAlignment(Element.ALIGN_LEFT);

		document.add(prghPartner);

		Paragraph prghPartnerName = new Paragraph(getPartnerName());
		prghPartnerName.setAlignment(Element.ALIGN_LEFT);

		document.add(prghPartnerName);

		document.add(empty1Line);

		Paragraph prghDesignatedAddress = new Paragraph("Rendeltetési hely: ", smallBold);

		document.add(prghDesignatedAddress);

		Paragraph prghPartnerAddress = new Paragraph(getPartnerAddress(), smallFont);

		document.add(prghPartnerAddress);

		document.add(empty1Line);

	}

	private static void createTable(Document document) throws DocumentException {

		PdfPTable table = new PdfPTable(4);

		table.setTotalWidth(PageSize.A4.getWidth() - 50);
		table.setLockedWidth(true);

		table.getDefaultCell().setBorder(0);

		PdfPCell c1 = new PdfPCell(new Phrase("Pozíció", smallBold));
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

		c1 = new PdfPCell(new Phrase("Raklapazonosító", smallBold));
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

		String sql = "SELECT position_name, part_number, pallet_quantity, pallet.pallet_id FROM pallet, positions, offload_list WHERE pallet_position = positions.position_id AND offload_list_task_number = ? AND pallet.pallet_id = offload_list.pallet_id ORDER BY position_name";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

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

				table.addCell(c1);
				table.addCell(c2);
				table.addCell(c3);
				table.addCell(c4);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	private static void addFooter(Document document, PdfWriter writer) throws DocumentException, IOException {

		Paragraph empty2Lines = new Paragraph();
		addEmptyLine(empty2Lines, 2);

		PdfContentByte cb = writer.getDirectContent();

		BaseFont bf_times_bold = BaseFont.createFont(BaseFont.TIMES_BOLD, "Cp1252", false);
		BaseFont bf_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", false);

		cb.beginText();
		cb.setFontAndSize(bf_times, 14);
		cb.setTextMatrix(225, 180);
		cb.showText("Összesen: " + getTotalPallets() + " db raklap");
		cb.endText();

		cb.beginText();
		cb.setFontAndSize(bf_times_bold, 12);
		cb.setTextMatrix(70, 110);
		cb.showText("Kitárolást elvégezte");
		cb.endText();

		cb.beginText();
		cb.setFontAndSize(bf_times_bold, 12);
		cb.setTextMatrix(430, 110);
		cb.showText("Teherautóra rakta");
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

	private static String getComments() {

		String comments = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT comments from offload_list WHERE offload_list_task_number = ? LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				comments = rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		return comments;
	}

	private static String getCreationDate() {

		String creationDate = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT creation_date from offload_list WHERE offload_list_task_number = ? LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

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

	private static String getOffloadListNumber() {

		String offloadListNumber = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT offload_list_number from offload_list WHERE offload_list_task_number = ? LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				offloadListNumber = rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		return offloadListNumber;
	}

	private static String getCreator() {

		String creator = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT user_name FROM users, offload_list WHERE offload_list_task_number = ? AND offload_list.user_id = users.user_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

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

	private static String getPartnerName() {

		String partnerName = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_name FROM partners, pallet, offload_list WHERE offload_list_task_number = ? AND offload_list.pallet_id = pallet.pallet_id AND pallet.partner_id = partners.partner_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

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

	private static String getPartnerAddress() {

		String partnerAddress = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_address FROM partners WHERE partner_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, getPartnerName());

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				partnerAddress = rs.getString(1);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return partnerAddress;
	}

	private static int getTotalPallets() {

		int number = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT COUNT(*) FROM offload_list WHERE offload_list_task_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, offloadListTaskNum);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				number = rs.getInt(1);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return number;

	}

	private static BaseFont tryCreateBaseFont() throws DocumentException, IOException {
		BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.EMBEDDED);
		return baseFont;
	}

}
