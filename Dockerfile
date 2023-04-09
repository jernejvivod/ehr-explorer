FROM quay.io/wildfly/wildfly:26.1.2.Final-jdk17

ARG CORE_WAR_PATH="core/target/*.war"
ARG MIMIC_III_TARGET_EXTRACTION_WAR_PATH="mimic-iii-target-extraction/target/*.war"

COPY deploy/wildfly/standalone/configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration
ADD deploy/wildfly/modules /opt/jboss/wildfly/modules

COPY $CORE_WAR_PATH /opt/jboss/wildfly/standalone/deployments
COPY $MIMIC_III_TARGET_EXTRACTION_WAR_PATH /opt/jboss/wildfly/standalone/deployments
