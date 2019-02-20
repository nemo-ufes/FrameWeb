package br.ufes.inf.nemo.frameweb.codegenerator.engine;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.GeneralizationSet;

import br.ufes.inf.nemo.frameweb.model.frameweb.DAOClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.DomainClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.DomainMethod;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrameworkProfile;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerMethod;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerTemplate;
import br.ufes.inf.nemo.frameweb.model.frameweb.ORMTemplate;
import br.ufes.inf.nemo.frameweb.model.frameweb.ServiceClass;


/** WARNING!!
 * 
 * UGLY CODE BELOW
 * 
 */
public class TemplateEngine {
	
	public static String render(Element class_, FrameworkProfile frameworkTemplate) {

//		Nao havendo template definido, nada deve ser feito (talvez alertar alertar o
//		usuario com uma mensagem no console seja interessante).
//		Esse tratamento nao se aplica a camada de aplicacao, esta deve ser tratada futuramente.
		if (frameworkTemplate == null) {
			return null;
		}
		
		if (class_ instanceof DomainClass) {
			String generatedCode = renderDomainClass(
					(DomainClass) class_,
					(ORMTemplate) frameworkTemplate
			);
			
			return generatedCode;
		
		} else if (class_ instanceof Enumeration) {
			String generatedCode = renderEnumerationClass(
					(Enumeration) class_,
					(ORMTemplate) frameworkTemplate);
			
			return generatedCode;
			
		} else if (class_ instanceof FrontControllerClass) {
			String generatedCode = renderFrontControllerClass(
					(FrontControllerClass) class_,
					(FrontControllerTemplate) frameworkTemplate);

			return generatedCode;
			
		} else if (class_ instanceof DAOClass) {
//			code
			return null;
			
		} else if (class_ instanceof ServiceClass) {
//			code
			return null;
			
		} else {
			throw new IllegalArgumentException();
		}
		
	}

	public static String renderDomainClass(DomainClass domainClass, ORMTemplate ormTemplate) {
		
		String template = EngineUtils.decodeUrl(ormTemplate.getClassTemplate());
		
		TemplateEngineContext templateEngineContext = new TemplateEngineContext(template);
		
		templateEngineContext
			.addParameter("class", domainClass)
			.addParameter("package", domainClass.getPackage())
			.addParameter("attributes", domainClass.getAttributes())
			.addParameter("associations", domainClass.getAssociations())
			.addParameter("methods", domainClass.getOperations()
					.stream()
					.filter(DomainMethod.class::isInstance)
					.map(DomainMethod.class::cast)
					.collect(Collectors.toList())
			);
		
//		Consultar o orientador para melhor entendimento e aplicacao das generalizacoes
		try {
			List<Generalization> generalizations = domainClass.getGeneralizations();
			List<GeneralizationSet> generalizationSets = generalizations.get(0).getGeneralizationSets();
			GeneralizationSet generalizationSet = generalizationSets.get(0);

			templateEngineContext.addParameter("generalization", generalizationSet);

		} catch (NullPointerException | IndexOutOfBoundsException e) {
			templateEngineContext.addParameter("generalization", null);
		}

		return EngineUtils.sanitize(templateEngineContext.getCode());
	}
	
	public static String renderEnumerationClass(Enumeration enumerationClass, ORMTemplate ormTemplate) {
		String template = EngineUtils.decodeUrl(ormTemplate.getEnumerationClassTemplate());

		TemplateEngineContext templateEngineContext = new TemplateEngineContext(template);

		templateEngineContext
			.addParameter("package", enumerationClass.getPackage())
			.addParameter("class", enumerationClass)
			.addParameter("literals", enumerationClass.getOwnedLiterals());

		return EngineUtils.sanitize(templateEngineContext.getCode());
	}
	
	public static String renderFrontControllerClass(FrontControllerClass frontControllerClass,
			FrontControllerTemplate frontControllerTemplate) {
		
		String template = EngineUtils.decodeUrl(frontControllerTemplate.getClassTemplate());
		
		TemplateEngineContext templateEngineContext = new TemplateEngineContext(template);
		
		templateEngineContext
			.addParameter("package", frontControllerClass.getPackage())
			.addParameter("class", frontControllerClass)
			.addParameter("attributes", frontControllerClass.getAttributes())
			.addParameter("methods", frontControllerClass.getOperations()
				.stream()
				.filter(FrontControllerMethod.class::isInstance)
				.map(FrontControllerMethod.class::cast)
				.collect(Collectors.toList()));
		
		return null;
	}
	
}