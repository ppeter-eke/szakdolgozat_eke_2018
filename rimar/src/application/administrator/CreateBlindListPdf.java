package application.administrator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import application.LoginController;
import application.MySqlConnection;


public class CreateBlindListPdf {

	
	private static Font bigFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font smallFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font mediumFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.NORMAL);

	private static String partnerName;

	@SuppressWarnings("unused")
	public static void createBlindListPdf(String input) throws FileNotFoundException, DocumentException {

		partnerName = input;

		String dateTime = LocalDate.now().toString();

		try {
			Document document = new Document(PageSize.A4);

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream("blindlist_" + partnerName + "_" + dateTime + "_.pdf"));
			document.open();
			
			addMetaData(document);
			addHeader(document);
			createTable(document);
			
			document.close();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			partnerName = "";
		}

	}

	private static void addMetaData(Document document) {
		
		document.addTitle("RIMAR - Leltári vaklista: " + partnerName);
		document.addSubject("RIMAR Raktári Információs rendszer - Szakdolgozat 2018 EKE");
		document.addKeywords("Szakdolgozat, Java, iText");
		document.addAuthor("Pálosi Péter");
		document.addCreator("Pálosi Péter");
		
	}
	
	private static void addHeader(Document document) throws DocumentException {

		Paragraph empty1Line = new Paragraph();

		addEmptyLine(empty1Line, 1);

		document.add(empty1Line);

		Paragraph prghTitle = new Paragraph("Leltári vaklista", bigFont);
		prghTitle.setAlignment(Element.ALIGN_CENTER);

		document.add(prghTitle);

		Paragraph prghPartner = new Paragraph(partnerName, mediumFont);
		prghPartner.setAlignment(Element.ALIGN_CENTER);

		document.add(prghPartner);

		document.add(empty1Line);

		
		Paragraph prghUser = new Paragraph("Létrehozó: " + LoginController.getUsername(), smallFont);
		Paragraph prghTime = new Paragraph("Létrehozás dátuma: " + LocalDateTime.now().toString(), smallFont);
		Paragraph prghPalletNumber = new Paragraph("Raklapok száma: " + getNumberOfPallets(), smallFont);
		
		document.add(prghUser);
		document.add(prghTime);
		document.add(prghPalletNumber);

		addEmptyLine(empty1Line, 2);

		document.add(empty1Line);

	}
	
	
	private static void createTable(Document document) throws DocumentException {
		
		PdfPTable table = new PdfPTable(4);
		table.setTotalWidth(PageSize.A4.getWidth() - 50);
		table.setLockedWidth(true);
		
		PdfPCell c1 = new PdfPCell(new Phrase("Pozíció", smallBold));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);

		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Cikkszám", smallBold));
		
		
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Mennyiség", smallBold));
		
		
		c1.setPadding(10);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Raklapazonosító", smallBold));
		
		
		c1.setPadding(10);
		table.addCell(c1);

		table.setHeaderRows(1);
	
		fillTableData(table);
		
		
		document.add(table);
		
	}
	
	
	private static void fillTableData(PdfPTable table) {
		
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "SELECT position_name, pallet_id FROM pallet, partners, positions WHERE pallet.pallet_position = positions.position_id AND pallet.partner_id = partners.partner_id AND partner_name = ? AND archived = 0 AND offloaded = 0 ORDER BY position_name";
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			
			ps.setString(1, partnerName);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				PdfPCell c1 = new PdfPCell(new Phrase(rs.getString(1), smallFont));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c2 = new PdfPCell(new Phrase(" 		", smallFont));
				c2.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c3 = new PdfPCell(new Phrase("			", smallFont));
				c3.setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell c4 = new PdfPCell(new Phrase(rs.getString(2), smallFont));
				c4.setHorizontalAlignment(Element.ALIGN_CENTER);
				
			
				table.addCell(c1);
				table.addCell(c2);
				table.addCell(c3);
				table.addCell(c4);
			}
			
			
		}catch(SQLException e) {
			
			e.printStackTrace();
		}finally {
			
			myConnection.disconnect();
		}
	}
	
	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
	private static int getNumberOfPallets() {
		
		int number = 0;
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "SELECT COUNT(*) FROM pallet, partners where pallet.partner_id = partners.partner_id AND partner_name = ? AND archived = 0 AND offloaded = 0";
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			
			ps.setString(1, partnerName);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				number = rs.getInt(1);
				
			}
			
		}catch(SQLException e) {
			
			e.printStackTrace();
		}finally {
			myConnection.disconnect();
		}
		
		
		return number;
	}

}
