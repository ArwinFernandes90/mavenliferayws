package my_first_mvn_module.portlet;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.comparator.FileVersionVersionComparator;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserModel;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import my_first_mvn_module.constants.My_first_mvn_modulePortletKeys;

/**
 * @author SESA612648
 */
@Component(
	immediate = true,	
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=My_first_mvn_module",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + My_first_mvn_modulePortletKeys.MY_FIRST_MVN_MODULE,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class My_first_mvn_modulePortlet extends MVCPortlet {
	private static final Log _log = LogFactoryUtil.getLog(My_first_mvn_modulePortlet.class.getName());
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		// TODO Auto-generated method stub
		//FooLocalService.
		_log.info("Inside doView methd");
		super.doView(renderRequest, renderResponse);
	}
	
	@ProcessAction(name="test")
	public void actionMethodFromLiferayTag(ActionRequest request, ActionResponse response)
			throws IOException, PortletException, PortalException, SystemException{
		String param=ParamUtil.getString(request,"name");
		_log.info("parameter is ==>"+param);
		/*START
		 * Liferay generates a new version whenever we are updating any web content. 
		 * You can delete all older versions of web content.
		 * In this code I am deleting all the versions and keeping only original version and the latest version
		*/
		if(param.equalsIgnoreCase("WebContent")) {
		List<JournalArticleResource> journalArticleResources = 
				JournalArticleResourceLocalServiceUtil.getJournalArticleResources(-1, -1);
			    //JournalArticleLocalServiceUtil.getJournalArticleResources(start, end);
		
		for (JournalArticleResource
				journalArticeResource : journalArticleResources) {
			    DynamicQuery dynamicQuery =journalArticleLocalService.dynamicQuery();
			      //  DynamicQueryFactoryUtil.forClass(JournalArticle.class)
			    dynamicQuery.setProjection(ProjectionFactoryUtil.projectionList()
			                .add(ProjectionFactoryUtil.property("id"))
			                .add(ProjectionFactoryUtil.property("version"))
			                .add(ProjectionFactoryUtil.property("status"))
			    			.add(ProjectionFactoryUtil.property("articleId")))
			            .add(PropertyFactoryUtil.forName("groupId")
			                .eq(journalArticeResource.getGroupId()))
			            .add(PropertyFactoryUtil.forName("articleId")
			                .eq(journalArticeResource.getArticleId()))
			            .addOrder(OrderFactoryUtil.asc("version"));

			    List<Object[]> result =
			        JournalArticleLocalServiceUtil.dynamicQuery(dynamicQuery);

			    List<Double> journalArticlesVersionsToDelete =
			    		new ArrayList<Double>();

			    	for (int i=0; i < result.size(); i++) {
			    		long id = (long) result.get(i)[0];
			    		double version = (double) result.get(i)[1];
			    		int status = (int) result.get(i)[2];
			    		if ((status == WorkflowConstants.STATUS_APPROVED) || (status == WorkflowConstants.STATUS_EXPIRED)) {

					
					  if (i < 1) {
						  continue; 	
						  }
					  
					
					  if (i >= (result.size() - 1)) { 
						  continue;
						  }
					  
							  journalArticlesVersionsToDelete.add(version);					  
			    		}		    		  
			    	}
			
			    	for (double versionToDelete : journalArticlesVersionsToDelete) { 
						  {
							  JournalArticleLocalServiceUtil.deleteArticle(journalArticeResource.getGroupId(), 
									  journalArticeResource.getArticleId(), versionToDelete,
									  null, null); 
						  } 
						}
			 
			}
		}
		/*END
		 * Liferay generates a new version whenever we are updating any web content. 
		 * You can delete all older versions of web content.
		 * In this code I am deleting all the versions and keeping only original version and the latest version
		*/
		
		/*START
		 * Liferay generates a new version whenever we are updating any document details. 
		 * You can delete all older versions of your document.
		 * In this code, I am deleting all the older versions and keeping only the latest version
		*/
		if(param.equalsIgnoreCase("Documents")) {
		List<FileEntry>  dLFileEntries=new ArrayList<FileEntry>();
		FileVersionVersionComparator comparator = new FileVersionVersionComparator();
		
		dLFileEntries=dlAppService.getFileEntries(37350, DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		for(FileEntry fileEntry:dLFileEntries) {
		for (FileVersion fileVersion: fileEntry.getFileVersions(WorkflowConstants.STATUS_APPROVED)) {
			_log.info("File Version is::::"+fileVersion.getVersion()+":::Compare result::  "+comparator.compare(fileEntry.getFileVersion(), fileVersion));
		    if (comparator.compare(fileVersion,fileEntry.getFileVersion()) > 0) {
		        dlAppService.deleteFileVersion(fileVersion.getFileEntryId(), fileVersion.getVersion());
		    }
		}
		
		}
		}
		/*END
		 * Liferay generates a new version whenever we are updating any document details. 
		 * You can delete all older versions of your document.
		 * In this code, I am deleting all the older versions and keeping only the latest version
		*/
		 DynamicQuery dynamicQuery =_userLocalService.dynamicQuery();
		 dynamicQuery.setProjection(ProjectionFactoryUtil.projectionList()
	                .add(ProjectionFactoryUtil.property("userId"))
	                .add(ProjectionFactoryUtil.property("companyId"))
	                .add(ProjectionFactoryUtil.property("status"))
	    			.add(ProjectionFactoryUtil.property("emailAddress")))
	            .add(PropertyFactoryUtil.forName("status")
	                .eq(5))
	            .addOrder(OrderFactoryUtil.asc("status"));
		 
		 List<Object[]> userList= _userLocalService.dynamicQuery(dynamicQuery);
		 _log.info("Value"+dynamicQuery.toString());
		 for(int i=0; i<userList.size();i++) {
			String email=(String)userList.get(i)[3];
			long userId=(long)userList.get(i)[0];
			if(email.equalsIgnoreCase("anonymous37310_1@liferay.com")) {
				_log.info("Inside \"anonymous37310_1@liferay.com\"");
				continue;
			}
			else {
				_log.info("Inside else");
				_userLocalService.deleteUser(userId);
			}
		 }

		 _log.info(userList.size());
	}
	
	@Reference
	UserLocalService _userLocalService;
	@Reference
	JournalArticleLocalService journalArticleLocalService;
	
	@Reference
	DLAppLocalService dLAppLocalService;
	@Reference
	DLAppService dlAppService;
}