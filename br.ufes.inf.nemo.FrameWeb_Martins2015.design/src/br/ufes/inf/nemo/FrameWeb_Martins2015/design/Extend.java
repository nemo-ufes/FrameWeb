package br.ufes.inf.nemo.FrameWeb_Martins2015.design;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreEList;

import br.ufes.inf.nemo.FrameWeb_Martins2015.*;


public class Extend {

	public Extend() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String printParameters(EObject m) {
		
		String result = null;
		
		EStructuralFeature ownedElement = m.eClass().getEStructuralFeature("ownedElement");
		EStructuralFeature name = m.eClass().getEStructuralFeature("name");
		EStructuralFeature methodType = null;
		EStructuralFeature ownedParameter = null;
		String methodType_value = null;
		Object parametros;
		
		String name_value = m.eGet(name).toString();
			
		result = name_value + " (";
		
		if(m.eClass().getEStructuralFeature("ownedParameter") != null){ 
			ownedParameter = m.eClass().getEStructuralFeature("ownedParameter");
			EcoreEList parameter_list = (EcoreEList) m.eGet(ownedParameter);
			
			int i;
			for(i=0;i<parameter_list.size();i++){
				EObject p = (EObject) parameter_list.get(i);
				//On Parameter
				String Type = "void";
				EStructuralFeature parameter_name_feature = p.eClass().getEAllAttributes().get(0);
				String parameter_name_value = p.eGet(parameter_name_feature).toString();
				if(p.eClass().getEStructuralFeature("type")!=null){
					EStructuralFeature parameter_type_feature = p.eClass().getEStructuralFeature("type");
					if(p.eGet(parameter_type_feature) != null){
						EObject parameter_type_object = (EObject) p.eGet(parameter_type_feature);
						EStructuralFeature parameter_type_name_feature = parameter_type_object.eClass().getEAllAttributes().get(0);
						Type = parameter_type_object.eGet(parameter_type_name_feature).toString();
					}
				}
				result = result + parameter_name_value + " : " + Type;
				if(i!=parameter_list.size()-1) result = result + ", ";
			}
			}
		
		result = result + ")";
		
		if(m.eClass().getEStructuralFeature("methodType") != null){
			result = result + " : ";
			methodType = m.eClass().getEStructuralFeature("methodType");
			methodType_value = m.eGet(methodType).toString();
			result = result + methodType_value;
			}else{
				result = result + " : " + "void";
			}
		
		return result;
	}
	
}
