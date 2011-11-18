package worker;

import java.io.Serializable;
import java.math.BigInteger;

public class ResultMessage implements Serializable {
	private static final long serialVersionUID = -6065578273626197783L;

	private BigInteger result;
	public ResultMessage(BigInteger result) {
		this.result = result;
	}
	public BigInteger getResult() {
		return this.result;
	}
}