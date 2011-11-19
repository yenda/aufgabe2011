package worker;

public class GetStatsMessage {
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
