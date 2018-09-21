# Deploying to Azure

# Azure CLI Setup

Install and authenticate with Azure CLI.

Create a resource group: `az group create -n <resourceGroup> -l westus`

Configure the location and RG as default to avoid having to specify them in every `az` command.
    
    az configure --defaults location=westus group=<resourceGroup>

# First Run (in-memory database)

In the `pom.xml`, go to [lines 224-225](https://github.com/brunoborges/spring-petclinic/blob/master/pom.xml#L224-L225), and change application name to a unique value and set to the resource group created above. Note the app name and replace where `<appname>` appears in the instructions below.

Build with Maven: `mvn package`

Deploy: `mvn azure-webapp:deploy`

This will create and deploy the web app using App Service Web App for Linux (not Web App for Containers) with Tomcat and Java 8 as configured in the `pom.xml`.

# Enable logs

```shell
az webapp log config --name <appname> \
  --web-server-logging filesystem

az webapp log tail --name <appname>
```

# Create MySQL database

Create instance (in westus2 due to availability of sku)
```shell
az mysql server create --name <dbname> --admin-user petclinicadmin --admin-password "<password>" \
  --sku-name GP_Gen5_2 --ssl-enforcement Disabled --version 5.7 --location westus2
```

Open Firewall for Azure services
```shell
az mysql server firewall-rule create --name petclinicdb2firewall \
   --server <dbname> \
   --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0
```

# Create MySQL database and user (using MySQL CLI)

Use the Azure Cloud Shell for this step due to firewall restriction (otherwise, you have to open MySQL to your own IP address or the wide internet).

Go to shell.azure.com.

Ensure Azure CLI is in the same subscription as before. 

$ mysql -upetclinicadmin@<dbname> -h<dbname>.mysql.database.azure.com -p

```sql
SQL> create database petclinic;
SQL> grant all privileges on petclinic.* to petclinicapp@'%' identified by '<password>';
SQL> flush privileges;
```

# Second run

Now you have to set environment variables on your Web App settings to enable the MySQL profile and set username/password/hostname of the MySQL database.

```shell
az webapp config appsettings set -n <appname> \
--settings SPRING_PROFILES_ACTIVE=mysql \
 MYSQL_DB="jdbc:mysql://<dbname>.mysql.database.azure.com:3306/petclinic?useUnicode=true" \
 MYSQL_USER="petclinicapp@<dbname>" \
 MYSQL_PASS="<password>"
```

Restart the application with `az webapp restart -n <appname>`
