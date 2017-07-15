package com.anyasoft.es.surveyapp.domains;

import java.util.ArrayList;

public class ServierActivities{
	//private ArrayList<StatusCountsItem> statusCounts;
	private ArrayList<SurveyCriteriaItem> surveyCriteria;
	private String surveyActivityId;
	public String getSurveyActivityId() {
		return surveyActivityId;
	}
	public void setSurveyActivityId(String surveyActivityId) {
		this.surveyActivityId = surveyActivityId;
	}
/*	public void setStatusCounts(ArrayList<StatusCountsItem> statusCounts){
		this.statusCounts = statusCounts;
	}
	public ArrayList<StatusCountsItem> getStatusCounts(){
		return statusCounts;
	}*/
	public void setSurveyCriteria(ArrayList<SurveyCriteriaItem> surveyCriteria){
		this.surveyCriteria = surveyCriteria;
	}
	public ArrayList<SurveyCriteriaItem> getSurveyCriteria(){
		return surveyCriteria;
	}

}