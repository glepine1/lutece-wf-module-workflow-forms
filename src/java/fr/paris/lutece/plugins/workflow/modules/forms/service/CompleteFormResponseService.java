package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.IEntryDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseDAO;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ICompleteFormResponseValueDAO;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;

public class CompleteFormResponseService implements ICompleteFormResponseService {

	@Inject
    private ICompleteFormResponseDAO _completeFormResponseDAO;
	
	@Inject
	private ICompleteFormResponseValueDAO _completeFormResponseValueDAO;
	
	@Inject
	private IEntryDAO _entryDAO;
	
	@Override
	public List<Question> findListQuestionWithoutResponse( FormResponse formResponse )
	{
		List<Question> listQuestionForm = QuestionHome.getListQuestionByIdForm( formResponse.getFormId( ) );
		List<FormQuestionResponse> listFormQuestionResponses = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
		
		List<Question> listQuestionWithoutResponse = new ArrayList<>( );
		
		for ( Question question : listQuestionForm)
		{
			FormQuestionResponse formQuestionResponse = listFormQuestionResponses.stream( )
					.filter( fqr -> fqr.getQuestion( ).getId( ) == question.getId( ) )
					.findFirst( ).orElse( null );
			
			if ( formQuestionResponse == null || CollectionUtils.isEmpty( formQuestionResponse.getEntryResponse( ) ) )
			{
				listQuestionWithoutResponse.add( question );
			}
		}
		return listQuestionWithoutResponse;
	}

	@Override
	public CompleteFormResponse find( int nIdHistory, int nIdTask )
	{
		CompleteFormResponse resubmitFormResponse = _completeFormResponseDAO.load( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        if ( resubmitFormResponse != null )
        {
        	resubmitFormResponse.setListCompleteReponseValues( _completeFormResponseValueDAO.load( resubmitFormResponse.getIdHistory( ), WorkflowUtils.getPlugin( ) ) );
        }

        return resubmitFormResponse;
	}
	
	@Override
	public List<Entry> getInformationListEntries( int nIdHistory )
	{
		Plugin plugin = WorkflowUtils.getPlugin( );
		
		List<CompleteFormResponseValue> listEditRecordValues = _completeFormResponseValueDAO.load( nIdHistory, plugin );
		List<Entry> listEntries = new ArrayList<>( );
		for ( CompleteFormResponseValue value : listEditRecordValues )
		{
			Entry entry = _entryDAO.load( value.getIdEntry( ), plugin );
				
			if ( entry != null )
            {
                listEntries.add( entry );
            }
		}
		return listEntries;
	}
}
