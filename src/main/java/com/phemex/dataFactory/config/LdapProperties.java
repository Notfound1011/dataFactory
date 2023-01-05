package com.phemex.dataFactory.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.config.LdapProperties
 * @Date: 2022年05月12日 14:58
 * @Description:
 */
@Getter
@Setter
public class LdapProperties {
    private String adminName = "administrator@cmex.corp";

    private String password = "O&77ip*KVkJ*!Y)fzsZt*f3jtW..%;SE";

    private String ldapUrl = "ldap://10.5.101.105:389";

    private String searchBase = "cn=users,dc=cmex,dc=corp";

    private String[] groupFilter = {"confluence-users", "confluence-administrators", "DEV", "Cd", "Dev", "Users", "Domain Admins", "RedisData", "CBT", "Admin", "Infra"};

    private String[] returningAttributes = {"memberOf", "url", "whenChanged", "employeeID", "name", "userPrincipalName", "physicalDeliveryOfficeName", "departmentNumber", "telephoneNumber", "homePhone", "mobile", "department", "sAMAccountName", "whenChanged", "mail"};

}
