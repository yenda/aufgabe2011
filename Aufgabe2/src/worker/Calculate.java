package worker;

import java.math.BigInteger;
import java.security.SecureRandom;

import akka.actor.ActorRef;

public class Calculate implements Runnable{
	
	private boolean stopRequested = false;
	private BigInteger N;
	private ActorRef master;
	private int calculID;
	
	private volatile long CPU;
	private volatile int Rho = 0;
	
	public long getCPU() {
		return CPU;
	}
	public int getRho() {
		return Rho;
	}
	public BigInteger getN() {
		return N;
	}
	
	public Calculate (BigInteger N, int calculID, ActorRef worker){
		this.N = N;
		this.master = worker;
		this.calculID = calculID;
	}

	@Override
	public void run(){
		this.N = calculate(N);
		ResultMessage resultMessage = new ResultMessage(N, calculID);
		master.tell(resultMessage);		
	}


	/**
	 * Takes 2 BigIntegers and return the biggest common divisor.
	 * 
	 * @param d
	 *            : BigInteger
	 * @param e
	 *            : BigInteger
	 * @return BigInteger
	 */
	private BigInteger calculate(BigInteger N){
		long CPUcycles = System.nanoTime();
		// if 2 is a factor of N it's immediately returned
		if(N.mod(new BigInteger("2")).compareTo(BigInteger.ZERO) == 0) return new BigInteger("2");
			
		SecureRandom rand;
		BigInteger a;
		BigInteger x;
		BigInteger y;
		BigInteger d;
		BigInteger p = N;
		do {
			rand = new SecureRandom();
			a = new BigInteger(N.bitLength(), rand);
			do {
				x = new BigInteger(N.bitLength(), rand);
			} while (x.compareTo(N) >= 0);

			y = x;

			do {
				this.Rho++;
				x = ((x.pow(2)).add(a)).mod(N);
				y = ((y.pow(2)).add(a)).mod(N);
				y = ((y.pow(2)).add(a)).mod(N);
				d = (y.subtract(x)).mod(N);
				p = d.gcd(N);
			} while ((p.compareTo(BigInteger.ONE) == 0)&&!this.stopRequested);
		} while ((!p.isProbablePrime(20))&&!this.stopRequested);
		this.CPU += System.nanoTime() - CPUcycles;
		return p;
	}
	
	public void requestStop() {
		  stopRequested = true;
	}
}
