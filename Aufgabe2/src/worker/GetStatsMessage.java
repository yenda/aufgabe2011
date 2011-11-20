package worker;

import java.io.Serializable;

public class GetStatsMessage implements Serializable {

	private static final long serialVersionUID = 2071284281046963431L;
	private Long CPUTime;
	private Integer cycles;
		
	public GetStatsMessage(Long cPUTime, Integer cycles) {
		super();
		CPUTime = cPUTime;
		this.cycles = cycles;
	}
	
	public GetStatsMessage() {
		super();
		CPUTime = 0l;
		this.cycles = 0;
	}
	
	public Long getCPUTime() {
		return CPUTime;
	}
	public Integer getCycles() {
		return cycles;
	}

}
