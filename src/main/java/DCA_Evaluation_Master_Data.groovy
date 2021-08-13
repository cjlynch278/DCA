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

loggerApi.info("---------------------------------------------------------------------------------------------------------")

def metricAttributeId = string2Uuid("ec9180ea-dc3e-4f9a-95d9-157e1a685269")
def uniqueIdentifierId = string2Uuid("a6c7a5b4-135e-4a58-8239-832b796ce668")
def multiSystemId = string2Uuid("bcabba1e-29dc-43f9-8171-db32047b7717")
def hierarchicalId = string2Uuid("a447da3f-3aac-43ee-9f33-71e7a8debb2c")
def dataTypeId = string2Uuid("5530400f-2aa8-4370-b3de-c17b07ab8539")

def mdeIDKey = "79dee746-0e57-48e2-bff5-a265f9313320"
def sdeIDKey = "2d917324-e567-43ec-ac1d-449256282ff7"


//Keys for relationships
def UIFRelationship = 'd6ffc755-1bcb-4461-ad77-32c1054a9b12'
def columnRelationship = '97023b62-67a4-48f7-beba-972a2239e804'

def sdeBool = false
def mdeBool = false
def kpiBool = false
def uniqueIdBool= false
def multiSystemBool = false
def hierarchicalBool = false


//Get all attributes of currently selected asset
def relationsResponse = relationApi.findRelations(
	FindRelationsRequest.builder()
		.sourceId(item.id)
		.build()
	)

for(relation in relationsResponse.results[0]) {
	
	Asset currentAsset = assetApi.getAsset(relation.target.id)
	currentAssetType = currentAsset.getType().getId().toString()
	uifOrColumRelationBool = false
	if(relation.getType().getId().toString() == UIFRelationship || relation.getType().getId().toString() == columnRelationship) {
		uifOrColumRelationBool = true
	}
	
	currentAssetStatusBool = false
	if(currentAsset.getStatus().getName().toString() == 'Approved')
		currentAssetStatusBool = true
	
	if(currentAssetType == mdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		mdeBool = true
	}
	else if(currentAssetType == sdeIDKey && currentAssetStatusBool && uifOrColumRelationBool) {
		sdeBool = true
	}
	
}

//Get Used in Enterprise Metric or KPI attribute
kpiBool = getAttribute(metricAttributeId)
loggerApi.info("KPI Bool: " + kpiBool)

uniqueIdBool = getAttribute(uniqueIdentifierId)
loggerApi.info("Unique ID Bool: " + uniqueIdBool)

multiSystemBool = getAttribute(multiSystemId)
loggerApi.info("MultiSystem Bool: " + multiSystemBool)

hierarchicalBool = getAttribute(hierarchicalId)
loggerApi.info("Hierarchical Bool: " + hierarchicalBool)

loggerApi.info("Sde Bool: " + sdeBool)
loggerApi.info("MDE Bool: " + mdeBool)


if( mdeBool == null && sdeBool == null && kpiBool == null && multiSystemBool == null && uniqueIdBool == null && hierarchicalBool == null)
	setDcaAttribute("Needs Assessment")
else if(mdeBool) 
	setDcaAttribute("Enterprise Master Data")
else if(sdeBool)
	setDcaAttribute("Enterprise Shared Data")
else if(kpiBool)
	setDcaAttribute("Enterprise Master Data")
else if(multiSystemBool == false)
	setDcaAttribute("Single Application Data")
else if(multiSystemBool == null)
	setDcaAttribute("Needs Assessment")
else if(uniqueIdBool)	
	setDcaAttribute("Enterprise Master Data")
else if(hierarchicalBool)
	setDcaAttribute("Enterprise Master Data")
else if(!hierarchicalBool )
	setDcaAttribute("Enterprise Shared Data")
else
	loggerApi.warn("No logic exists to change DCA type")



def setDcaAttribute(dataValue) {
	
	assetApi.setAssetAttributes(
		SetAssetAttributesRequest.builder()
			.assetId(item.id)
			.values([dataValue])
			.typeId(string2Uuid("5530400f-2aa8-4370-b3de-c17b07ab8539"))
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
