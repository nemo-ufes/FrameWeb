package br.ufes.inf.nemo.frameweb.codegenerator.e4.models;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

import br.ufes.inf.nemo.frameweb.codegenerator.e4.ProjectProperties;
import br.ufes.inf.nemo.frameweb.codegenerator.e4.classes.ClassCodeGenerator;
import br.ufes.inf.nemo.frameweb.model.frameweb.ControllerPackage;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerClass;
import br.ufes.inf.nemo.frameweb.model.frameweb.FrontControllerTemplate;
import br.ufes.inf.nemo.frameweb.model.frameweb.NavigationModel;
import br.ufes.inf.nemo.frameweb.model.frameweb.Page;
import br.ufes.inf.nemo.frameweb.model.frameweb.ViewPackage;
import br.ufes.inf.nemo.frameweb.utils.IFileUtils;
import br.ufes.inf.nemo.frameweb.utils.IFolderUtils;

public class NavigationModelCodeGenerator implements ModelCodeGenerator {

	private List<ControllerPackage> controllerPackages;
	private List<ViewPackage> viewPackages;
	private FrontControllerTemplate frontControllerTemplate;
	private ProjectProperties projectConfiguration;

	public NavigationModelCodeGenerator(NavigationModel navigationModel,
			FrontControllerTemplate frontControllerTemplate, ProjectProperties projectConfiguration) {
		
		controllerPackages = navigationModel.getOwnedElements()
				.stream()
				.filter(ControllerPackage.class::isInstance)
				.map(ControllerPackage.class::cast)
				.collect(Collectors.toList());

		viewPackages = navigationModel.getOwnedElements()
				.stream()
				.filter(ViewPackage.class::isInstance)
				.map(ViewPackage.class::cast)
				.collect(Collectors.toList());

		this.frontControllerTemplate = frontControllerTemplate;
		this.projectConfiguration = projectConfiguration; 
	}

	@Override
	public void generate() {
		String templatePath = frontControllerTemplate.getClassTemplate();
		String template = projectConfiguration.getTemplate(templatePath);
		
		IFolder src = projectConfiguration.getSourceFolder();
		
		controllerPackages.forEach(controllerPackage -> {
			String packagePath = IFolderUtils.packageNameToPath(controllerPackage.getName());

			IFolderUtils.makeDirectory(src, packagePath);
			
			IFolder package_ = src.getFolder(packagePath);

			controllerPackage.getOwnedTypes()
					.stream()
					.filter(FrontControllerClass.class::isInstance)
					.map(FrontControllerClass.class::cast)
					.forEach(frontControllerClass -> {
						String code = ClassCodeGenerator.render(frontControllerClass, template);
						
						String fileName = frontControllerClass.getName() + projectConfiguration.getClassExtension();
						IFile file = package_.getFile(fileName);
						
						IFileUtils.createFile(file, code);
					});
		});
		
//		TODO As paginas web ainda nao foram trabalhadas, acredito que essa parte de geracao
//		da view necessite de adaptacoes. Temporariamente permanecera assim...
		String viewTemplatePath = frontControllerTemplate.getPageTemplate();
		String viewTemplate = projectConfiguration.getTemplate(viewTemplatePath);
		
		IFolder view = projectConfiguration.getViewFolder();
		
		viewPackages.forEach(viewPackage -> {
			viewPackage.getOwnedTypes()
					.stream()
					.filter(Page.class::isInstance)
					.map(Page.class::cast)
					.forEach(page -> {
						String code = ClassCodeGenerator.render(page, viewTemplate);
						
						String fileName = page.getName() + projectConfiguration.getPageExtension();
						IFile file = view.getFile(fileName);
								
						IFileUtils.createFile(file, code);
					});
		});
	}

}
