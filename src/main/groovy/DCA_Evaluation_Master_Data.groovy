import com.collibra.dgc.core.api.model.instance.attribute.Attribute
import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest
import com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest.Builder

import static com.collibra.dgc.core.api.dto.SortOrder.ASC
import static com.collibra.dgc.core.api.dto.instance.attribute.FindAttributesRequest.SortField.LAST_MODIFIED

import com.collibra.dgc.core.api.component.instance.AssetApi
import com.collibra.dgc.core.api.model.instance.Asset
import com.collibra.dgc.core.api.component.instance.RelationApi
import com.collibra.dgc.core.api.component.logger.LoggerApi
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest
import com.collibra.dgc.core.api.dto.instance.relation.FindRelationsRequest.Builder
import com.collibra.dgc.core.api.component.meta.AssetTypeApi
import com.collibra.dgc.core.api.component.meta.AttributeTypeApi
import com.collibra.dgc.core.api.dto.instance.complexrelation.AttributeValue
import com.collibra.dgc.core.api.dto.meta.assettype.FindAssetTypesRequest
import com.collibra.dgc.core.api.dto.meta.assettype.FindAssetTypesRequest.Builder
import com.collibra.dgc.core.api.model.meta.type.AssetType
import com.collibra.dgc.core.api.dto.instance.asset.SetAssetAttributesRequest.Builder
import com.collibra.dgc.core.api.dto.instance.asset.SetAssetAttributesRequest

import com.collibra.dgc.core.api.dto.instance.attribute.ChangeAttributeRequest

def metricAttributeId = string2Uuid(execution.getVariable("metricAttributeId"))
def uniqueIdentifierId = string2Uuid(execution.getVariable("uniqueIdentifierId"))
def multiSystemId = string2Uuid(execution.getVariable("multiSystemId"))
def hierarchicalId = string2Uuid(execution.getVariable("hierarchicalId"))
def dataTypeId = string2Uuid(execution.getVariable("dataTypeId"))
def entMdeIDKey = execution.getVariable("entMdeIDKey")
def mdeIDKey = execution.getVariable("mdeIDKey")
def sdeIDKey = execution.getVariable("sdeIDKey")
def singleAppDataKey = execution.getVariable("singleAppDataKey")




//Keys for relationships
def UIFRelationship = execution.getVariable("UIFRelationship")
def columnRelationship = execution.getVariable("columnRelationship")

def sdeBool = false
def mdeBool = false
def entMdeBool = false
def singleAppBool = false
def kpiBool = false
def uniqueIdBool= false
def multiSystemBool = false
def hierarchicalBool = false


//Get all source relations of currently selected asset
def relationsResponse = relationApi.findRelations(
	FindRelationsRequest.builder()
		.sourceId(item.id)
		.build()
	)

for(relation in relationsResponse.getResults()) {
	
	Asset currentAsset = assetApi.getAsset(relation.target.id)
	currentAssetType = currentAsset.getType().getId().toString()
	loggerApi.info("CurrentAsset type : " + currentAssetType)
	loggerApi.info("SDE Key " + sdeIDKey)
	loggerApi.info("Current Relation Type: " + relation.getType().getId().toString())
	
	uifOrColumRelationBool = false
	if(relation.getType().getId().toString() == UIFRelationship || relation.getType().getId().toString() == columnRelationship) {
		uifOrColumRelationBool = true
	}
	loggerApi.info("uifOrColumRelationBool: " + uifOrColumRelationBool)
	currentAssetStatusBool = false
	if(currentAsset.getStatus().getName().toString() == 'Approved' || currentAsset.getStatus().getName().toString() == 'Accepted') {
		loggerApi.info("Flipping status bool to true")
		currentAssetStatusBool = true
	}
	loggerApi.info("currentAssetStatusBool: " + currentAssetStatusBool)

	if(currentAssetType == mdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching mdeBool to True")
		mdeBool = true
	}
	else if(currentAssetType == sdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching sdeBool to True")
		sdeBool = true
	}
	else if(currentAssetType == singleAppDataKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching singleAppBool to True")
		singleAppBool = true
	}	
	else if(currentAssetType == entMdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching entMdeBool to True")
		entMdeBool = true
	}
	
}

//Get all target relations of currently selected asset
def targetRelationsResponse = relationApi.findRelations(
	FindRelationsRequest.builder()
		.targetId(item.id)
		.build()
	)

for(relation in targetRelationsResponse.getResults()) {
	
	Asset currentAsset = assetApi.getAsset(relation.source.id)
	currentAssetType = currentAsset.getType().getId().toString()
	loggerApi.info("CurrentAsset type : " + currentAssetType)
	loggerApi.info("SDE Key " + sdeIDKey)
	loggerApi.info("Current Relation Type: " + relation.getType().getId().toString())
	
	uifOrColumRelationBool = false
	if(relation.getType().getId().toString() == UIFRelationship || relation.getType().getId().toString() == columnRelationship) {
		uifOrColumRelationBool = true
	}
	loggerApi.info("uifOrColumRelationBool: " + uifOrColumRelationBool)
	
	currentAssetStatusBool = false
	if(currentAsset.getStatus().getName().toString() == 'Approved' || currentAsset.getStatus().getName().toString() == 'Accepted') {
		loggerApi.info("Flipping status bool to true")
		currentAssetStatusBool = true
	}
	loggerApi.info("currentAssetStatusBool: " + currentAssetStatusBool)

	if(currentAssetType == mdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching mdeBool to True")
		mdeBool = true
	}
	else if(currentAssetType == sdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching sdeBool to True")
		sdeBool = true
	}
	else if(currentAssetType == singleAppDataKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching singleAppBool to True")
		singleAppBool = true
	}	
	else if(currentAssetType == entMdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		loggerApi.info("Switching entMdeBool to True")
		entMdeBool = true
	}
	
	
}

loggerApi.info("SingleAppData Bool: " + singleAppBool)

uniqueIdBool = getAttribute(uniqueIdentifierId)
loggerApi.info("Unique ID Bool: " + uniqueIdBool)

multiSystemBool = getAttribute(multiSystemId)
loggerApi.info("MultiSystem Bool: " + multiSystemBool)

hierarchicalBool = getAttribute(hierarchicalId)
loggerApi.info("Hierarchical Bool: " + hierarchicalBool)

loggerApi.info("Sde Bool: " + sdeBool)
loggerApi.info("MDE Bool: " + mdeBool)
loggerApi.info("ENT MDE Bool: " + entMdeBool)


if( singleAppBool == false && entMdeBool == false && mdeBool == false && sdeBool == false && kpiBool == null && multiSystemBool == null && uniqueIdBool == null && hierarchicalBool == null)
	setDcaAttribute("Needs Assessment")
else if(mdeBool) 
	setDcaAttribute("Master Data")
else if(sdeBool)
	setDcaAttribute("Shared Data")
else if(entMdeBool){
	loggerApi.info("Changing attribute to enterprise Master Data" )
	setDcaAttribute("Enterprise Master Data")
}
else if (singleAppBool)
	setDcaAttribute("Single Application Data")
//else if(kpiBool)
	//("Enterprise Master Data")
else if(multiSystemBool == false)
	setDcaAttribute("Single Application Data")
else if(multiSystemBool == null)
	setDcaAttribute("Needs Assessment")
else if(uniqueIdBool)	
	setDcaAttribute("Master Data")
else if(hierarchicalBool)
	setDcaAttribute("Master Data")
else if(!hierarchicalBool )
	setDcaAttribute("Shared Data")
else
	loggerApi.warn("No logic exists to change DCA type")



def setDcaAttribute(dataValue) {
	
	assetApi.setAssetAttributes(
		SetAssetAttributesRequest.builder()
			.assetId(item.id)
			.values([dataValue])
			.typeId(string2Uuid(dataCriticalityAttributeId))
			.build()
		)
}
def changeAttribute(dataTypeAttributeId, dataValue) {
	attributeApi.changeAttribute(
		ChangeAttributeRequest.builder()
			.id(dataTypeAttributeId)
			.value(dataValue)
			.build()
		)
}
def getAttribute(attributeId) {
	try {
		assetAttributesResponse = attributeApi.findAttributes(
			FindAttributesRequest.builder()
				.assetId(item.id)
				.typeIds([attributeId])
				.build()
		) 
		loggerApi.info("Attributes Response: " + assetAttributesResponse)
		return assetAttributesResponse.results[0].value
	
	} catch(Exception e) {
		loggerApi.info("Exception: " + e)
		return null
	} 
	
}
