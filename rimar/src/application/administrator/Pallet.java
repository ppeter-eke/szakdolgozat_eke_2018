package application.administrator;

public class Pallet {

	private String palletId;
	private String partNumber;
	private double palletQuantity;
	private String palletPosition;
	private String truckLicense1;
	private String truckLicense2;
	private String deliverynoteNumber;
	private String arrivalDate;
	private int quarantine;

	public Pallet(String palletId, String partNumber, double palletQuantity, String palletPosition,
			String truckLicense1, String truckLicense2, String deliverynoteNumber, String arrivalDate, int quarantine) {

		this.palletId = palletId;
		this.partNumber = partNumber;
		this.palletQuantity = palletQuantity;
		this.palletPosition = palletPosition;
		this.truckLicense1 = truckLicense1;
		this.truckLicense2 = truckLicense2;
		this.deliverynoteNumber = deliverynoteNumber;
		this.arrivalDate = arrivalDate;
		this.quarantine = quarantine;
	}

	public Pallet(String palletId, String palletPosition, String partNumber, double palletQuantity) {

		this.palletId = palletId;
		this.partNumber = partNumber;
		this.palletQuantity = palletQuantity;
		this.palletPosition = palletPosition;
	}

	public Pallet(String palletId) {

		this.palletId = palletId;
	}

	public String getPalletId() {
		return palletId;
	}

	public void setPalletId(String palletId) {
		this.palletId = palletId;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public double getPalletQuantity() {
		return palletQuantity;
	}

	public void setPalletQuantity(double palletQuantity) {
		this.palletQuantity = palletQuantity;
	}

	public String getPalletPosition() {
		return palletPosition;
	}

	public void setPalletPosition(String palletPosition) {
		this.palletPosition = palletPosition;
	}

	public String getTruckLicense1() {
		return truckLicense1;
	}

	public void setTruckLicense1(String truckLicense1) {
		this.truckLicense1 = truckLicense1;
	}

	public String getTruckLicense2() {
		return truckLicense2;
	}

	public void setTruckLicense2(String truckLicense2) {
		this.truckLicense2 = truckLicense2;
	}

	public String getDeliverynoteNumber() {
		return deliverynoteNumber;
	}

	public void setDeliverynoteNumber(String deliverynoteNumber) {
		this.deliverynoteNumber = deliverynoteNumber;
	}

	public String getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public int getQuarantine() {
		return quarantine;
	}

	public void setQuarantine(int quarantine) {
		this.quarantine = quarantine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((palletId == null) ? 0 : palletId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pallet other = (Pallet) obj;
		if (palletId == null) {
			if (other.palletId != null)
				return false;
		} else if (!palletId.equals(other.palletId)) {
			return false;
		}
		return true;
	}

}