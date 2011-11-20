package worker;

import java.io.Serializable;
import java.math.BigInteger;

public class CalculateMessage implements Serializable {
	private static final long serialVersionUID = 840244832287440949L;
	private BigInteger N;
	private int calculID;

	public CalculateMessage(BigInteger N, int calculID) {
		this.N = N;
		this.calculID = calculID;
	}

	public BigInteger getN() {
		return N;
	}
	public int getCalculID() {
		return this.calculID;
	}
}
