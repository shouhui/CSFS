package core.dag;
import java.util.List;
import java.util.Map;

/*	DAG类*/
public class DAG {

	/*	任务列表*/
	private List<Cloudlet>cloudletList;
	
	/*	任务间传递数据量Map表*/
	private Map<String,Double>cloudletDependValueMap;

	/*	构造函数*/
	public DAG(){
		
	}
	
//	/*	提交cloudletList至DAG*/
//	public void submitCloudletList(List cl){
//		this.cloudletList = cl;
//	}
//	
//	/*	提交任务间依赖关系Map表*/
//	public void submitCloudletDependMap(Map cd){
//		this.cloudletDependMap = cd;
//	}
//	
//	/*	提交任务间依赖关系值Map表*/
//	public void submitCloudletDependValueMap(Map cdv){
//		this.cloudletDependValueMap = cdv;
//	}
//	
	/*	判断cloudlet间是否有依赖关系*/
	public boolean isDepend(String src,String des){
		if(cloudletDependValueMap.containsKey(src+" "+des)){
			return true;
		}
		else return false;
	}
	
	/*	获取cloudlet间依赖值*/
	public double getDependValue(int src,int des){
		return cloudletDependValueMap.get(String.valueOf(src)+" "+String.valueOf(des));
	}
	
	/*	设置cloudletList*/
	public void setCloudletList(List<Cloudlet> cl){
		this.cloudletList = cl;
	}
	/*	获得cloudletList*/
	public List<Cloudlet> getCloudletList(){
		return cloudletList;		
	}
	
	/*	设置cloudlet间依赖值*/
	public void setCloudletDependValueMap(Map<String, Double> cdv){
		this.cloudletDependValueMap = cdv;
	}
	/*	获得cloudlet间依赖值*/
	public Map<String, Double> getCloudletDependValueMap(){
		return cloudletDependValueMap;
	}

	/*	getCloudletById()*/
	public Cloudlet getCloudletById(int cloudletId){
		for(Cloudlet cl:cloudletList){
			if(cl.getCloudletId() == cloudletId)
				return cl;
		}
		return null;
	}
}
