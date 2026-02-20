from aws_cdk import (
    Stack,
    NestedStack,
    Fn,
    Aspects
)
from constructs import Construct
import os
from typing import Union
import json
import aws_cdk as cdk 
import aws_cdk.aws_iam as iam
import aws_cdk.aws_kms as kms
import aws_cdk.aws_ec2 as ec2
import aws_cdk.aws_iam as iam
import aws_cdk.aws_dynamodb as dynamodb
from cdk_nag import ( AwsSolutionsChecks, NagSuppressions )

from utils.utils_params import get_aws_service_instance, get_parameter_value, get_road_common_config,put_road_service_config,get_road_service_config,check_road_service_config_parameter_exist
from config.app_configuration import (
    ORGANIZATION,PROJECT, RESOURCE_NAME_PREFIX,LOGICAL_ID_PREFIX
)


class DynamoDbStack(NestedStack):

    def __init__(self, scope: Construct, construct_id: str,target_environment:str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)
        

        ddb_table_bookmark = dynamodb.Table(self, f"DDB{LOGICAL_ID_PREFIX}{target_environment.capitalize()}GlueJobSourceTableBookmark",
            table_name=f"{RESOURCE_NAME_PREFIX}_{target_environment.lower()}_admin_glue_job_source_table_bookmark",
            partition_key= dynamodb.Attribute(name="job_id", type=dynamodb.AttributeType.STRING),
            sort_key=dynamodb.Attribute(name="dbschema_and_table_name", type=dynamodb.AttributeType.STRING),
            billing_mode=dynamodb.BillingMode.PAY_PER_REQUEST,
            point_in_time_recovery= True 
        )
        
        self.ddb_table_bookmark = ddb_table_bookmark
        
        # Stack Outputs that are programmatically synchronized
        cdk.CfnOutput(
            self,
            f"DDB{LOGICAL_ID_PREFIX}{target_environment.capitalize()}GlueJobSourceTableBookmarkTableName",
            value=ddb_table_bookmark.table_name,
            export_name=f"DDB{LOGICAL_ID_PREFIX}{target_environment.capitalize()}GlueJobSourceTableBookmarkTableName"
        )

        ddb_table_ddl_deploy = dynamodb.Table(self, f"DDB{LOGICAL_ID_PREFIX}{target_environment.capitalize()}DBRelease",
            table_name=f"{RESOURCE_NAME_PREFIX}_{target_environment.lower()}_admin_db_release",
            partition_key= dynamodb.Attribute(name="database_id", type=dynamodb.AttributeType.STRING),
            billing_mode=dynamodb.BillingMode.PAY_PER_REQUEST,
            point_in_time_recovery= True 
        )
        
        self.ddb_table_ddl_deploy = ddb_table_ddl_deploy
        
        # Stack Outputs that are programmatically synchronized
        cdk.CfnOutput(
            self,
            f"DDB{LOGICAL_ID_PREFIX}{target_environment.capitalize()}DBReleaseTableName",
            value=ddb_table_ddl_deploy.table_name,
            export_name=f"DDB{LOGICAL_ID_PREFIX}{target_environment.capitalize()}DBReleaseTableName"
        )