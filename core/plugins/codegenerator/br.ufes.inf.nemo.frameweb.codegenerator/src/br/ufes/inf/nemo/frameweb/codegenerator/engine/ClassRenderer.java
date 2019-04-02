package br.ufes.inf.nemo.frameweb.codegenerator.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.GeneralizationSet;

import br.ufes.inf.nemo.frameweb.codegenerator.utils.IFileUtils;
import br.ufes.inf.nemo.frameweb.model.frameweb.DAOClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.DomainAssociation;
import br.ufes.inf.nemo.frameweb.model.frameweb.DomainClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.DomainMethod;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrameworkProfile;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerMethod;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerTemplate;
import br.ufes.inf.nemo.frameweb.model.frameweb.NavigationAssociation;
import br.ufes.inf.nemo.frameweb.model.frameweb.ORMTemplate;
import br.ufes.inf.nemo.frameweb.model.frameweb.Page;
import br.ufes.inf.nemo.frameweb.model.frameweb.ServiceClass;


/** WARNING!!
 * 
 * UGLY CODE BELOW
 * 
 */
public class ClassRenderer {
	
	private Element class_;
	private FrameworkProfile frameworkTemplate;
	private IFolder templateFolder;
	
	public ClassRenderer(Element class_, FrameworkProfile frameworkTemplate, IFolder templateFolder) {
		this.class_ = class_;
		this.frameworkTemplate = frameworkTemplate;
		this.templateFolder = templateFolder;
	}
	
	public String render() {

//		Nao havendo template definido, nada deve ser feito (talvez alertar alertar o
//		usuario com uma mensagem no console seja interessante).
//		Esse tratamento nao se aplica a camada de aplicacao, esta deve ser tratada futuramente.
		if (frameworkTemplate == null) {
			return null;
		}
		
		if (class_ instanceof DomainClass) {
			return renderDomainClass();
		
		} else if (class_ instanceof Enumeration) {
			return renderEnumerationClass();
			
		} else if (class_ instanceof FrontControllerClass) {
			return renderFrontControllerClass();

		} else if (class_ instanceof Page) {
			return renderPage();
			
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

	public String renderDomainClass() {
		DomainClass domainClass = (DomainClass) class_;
		ORMTemplate ormTemplate = (ORMTemplate) frameworkTemplate;

		IFile classTemplateFile = templateFolder.getFile(ormTemplate.getClassTemplate());
		String classTemplate = IFileUtils.getText(classTemplateFile);

//		TODO injetar uma implementacao de template por parte do usuario, permitindo que outras engines sejam utilizadas
		TemplateEngine templateEngineContext = new JtwigTemplateEngineImpl();
		templateEngineContext.setTemplate(classTemplate);

		templateEngineContext
			.addParameter("package", domainClass.getPackage())
			.addParameter("class", domainClass)
			.addParameter("attributes", domainClass.getAttributes())
			.addParameter("associations", domainClass.getAssociations()
					.stream()
					.map(DomainAssociation.class::cast)
					.collect(Collectors.toList()))
			.addParameter("methods", domainClass.getOperations()
					.stream()
					.filter(DomainMethod.class::isInstance)
					.map(DomainMethod.class::cast)
					.collect(Collectors.toList()));
		
		List<Generalization> generalizations = domainClass.getGeneralizations();
		
		if (!generalizations.isEmpty()) {
			List<GeneralizationSet> generalizationSets = new ArrayList<GeneralizationSet>();
			
			generalizations.forEach(generalization -> {
				generalizationSets.addAll(generalization.getGeneralizationSets());
			});
			
			templateEngineContext.addParameter("generalizations", generalizationSets);
			
		} else {
			templateEngineContext.addParameter("generalizations", new ArrayList<>());
		}
		
		return templateEngineContext.getCode();
	}
	
	public String renderEnumerationClass() {
		Enumeration enumerationClass = (Enumeration) class_;
		ORMTemplate ormTemplate = (ORMTemplate) frameworkTemplate;
				
		IFile enumerationClassTemplateFile = templateFolder.getFile(ormTemplate.getEnumerationClassTemplate());
		String enumerationClassTemplate = IFileUtils.getText(enumerationClassTemplateFile);

//		TODO injetar uma implementacao de template por parte do usuario, permitindo que outras engines sejam utilizadas
		TemplateEngine templateEngineContext = new JtwigTemplateEngineImpl();
		templateEngineContext.setTemplate(enumerationClassTemplate);
		
		templateEngineContext
			.addParameter("package", enumerationClass.getPackage())
			.addParameter("class", enumerationClass)
			.addParameter("literals", enumerationClass.getOwnedLiterals()
					.stream()
					.map(EnumerationLiteral::getName)
					.collect(Collectors.toList()));
		
		return templateEngineContext.getCode();
	}
	
	public String renderFrontControllerClass() {
		FrontControllerClass frontControllerClass = (FrontControllerClass) class_;
		FrontControllerTemplate frontControllerTemplate = (FrontControllerTemplate) frameworkTemplate;
		
		IFile frontControllerClassTemplateFile = templateFolder.getFile(frontControllerTemplate.getClassTemplate());
		String frontControllerClassTemplate = IFileUtils.getText(frontControllerClassTemplateFile);
		
//		TODO injetar uma implementacao de template por parte do usuario, permitindo que outras engines sejam utilizadas
		TemplateEngine templateEngineContext = new JtwigTemplateEngineImpl();
		templateEngineContext.setTemplate(frontControllerClassTemplate);

		templateEngineContext
			.addParameter("package", frontControllerClass.getPackage())
			.addParameter("class", frontControllerClass)
			.addParameter("attributes", frontControllerClass.getAttributes())
			.addParameter("associations", frontControllerClass.getAssociations()
					.stream()
					.map(NavigationAssociation.class::cast)
					.collect(Collectors.toList()))
			.addParameter("methods", frontControllerClass.getOperations()
					.stream()
					.filter(FrontControllerMethod.class::isInstance)
					.map(FrontControllerMethod.class::cast)
					.collect(Collectors.toList()));
		
		List<Generalization> generalizations = frontControllerClass.getGeneralizations();
		
		if (!generalizations.isEmpty()) {
			List<GeneralizationSet> generalizationSets = new ArrayList<GeneralizationSet>();
			
			generalizations.forEach(generalization -> {
				generalizationSets.addAll(generalization.getGeneralizationSets());
			});
			
			templateEngineContext.addParameter("generalizations", generalizationSets);
			
		} else {
			templateEngineContext.addParameter("generalizations", new ArrayList<>());
		}
		
		return templateEngineContext.getCode();
	}
	
//	TODO implementar um renderizador de paginas
	@SuppressWarnings("unused")
	public String renderPage() {
		Page page = (Page) class_;
		FrontControllerTemplate frontControllerTemplate = (FrontControllerTemplate) frameworkTemplate;
		
		IFile pageTemplateFile = templateFolder.getFile(frontControllerTemplate.getPageTemplate());
		String pageTemplate = IFileUtils.getText(pageTemplateFile);
		
		return pageTemplate;
	}
	
}