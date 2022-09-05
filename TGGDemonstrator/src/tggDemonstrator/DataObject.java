package tggDemonstrator;


public class DataObject {
	public enum Modelgeneration {NEW_MODEL, LOAD_MODEL, LOAD_SRC_CREATE_TRG, CREATE_SRC_LOAD_TRG} //LOAD_MODEL = LOAD_SRC_LOAD_TRG; NEW_MODEL = CREATE_SRC_CREATE_TRG
	private Modelgeneration modelgenOption;
	private String sourcePath;
	private String targetPath;
	private String corrPath;
	private String protocolPath;
	
	/*
	 * Class constructor defines the main data that is needed for instance
	 */
	public DataObject(String sourcePath, String targetPath, String corrPath, String protocolPath, Modelgeneration modelgenOption) {
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.corrPath = corrPath;
		this.protocolPath = protocolPath;
		this.modelgenOption = modelgenOption;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	
	public String getTargetPath() {
		return targetPath;
	}
	
	public String getCorrPath() {
		return corrPath;
	}
	
	public String getProtocolPath() {
		return protocolPath;
	}
	
	public Modelgeneration getModelgenerationType() {
		return modelgenOption;
	}
}
