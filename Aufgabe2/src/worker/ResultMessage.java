package worker;

import java.io.Serializable;
import java.math.BigInteger;

public class ResultMessage implements Serializable {
	private static final long serialVersionUID = -6065578273626197783L;

	private BigInteger result;
	private int calculID;
	public ResultMessage(BigInteger result, int calculID) {
		this.result = result;
		this.calculID = calculID;
	}
	public BigInteger getResult() {
		return this.result;
	}
	public int getCalculID() {
		return this.calculID;
	}
}