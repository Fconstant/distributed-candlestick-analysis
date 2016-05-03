package felipe.luciano.finances;

import java.io.Serializable;

import org.joda.time.LocalDate;

import felipe.luciano.broadcast.BroadcastReceiver;
import felipe.luciano.broadcast.BroadcastSender;

public class Candlestick implements Serializable{

	private static final long serialVersionUID = 8416011468385114741L;
	
	private LocalDate date;
	private double open;
	private double high;
	private double close;
	private double low;

	public Candlestick(LocalDate date, double open, double high, double low, double close) {		
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}

	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}

	@Override
	public String toString() {
		return "Candlestick [date=" + date + ", open=" + open + ", high=" + high + ", close=" + close + ", low="
				+ low + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(close);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		temp = Double.doubleToLongBits(high);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(low);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(open);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Candlestick other = (Candlestick) obj;
		if (Double.doubleToLongBits(close) != Double.doubleToLongBits(other.close))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (Double.doubleToLongBits(high) != Double.doubleToLongBits(other.high))
			return false;
		if (Double.doubleToLongBits(low) != Double.doubleToLongBits(other.low))
			return false;
		if (Double.doubleToLongBits(open) != Double.doubleToLongBits(other.open))
			return false;
		return true;
	}
	

}
