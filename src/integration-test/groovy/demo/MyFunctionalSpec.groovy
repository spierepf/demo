package demo

import grails.testing.mixin.integration.Integration
import grails.transaction.*

import geb.spock.*
import spock.lang.Shared

import grails.plugins.rest.client.RestResponse
import grails.plugins.rest.client.RestBuilder
import grails.plugin.springsecurity.ui.RegistrationCode

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
@Rollback
class MyFunctionalSpec extends GebSpec {

    @Shared RestBuilder rest = new RestBuilder()

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        given:"a user account is created and given a registration code"
            def user = new User(username:"username", password:"password", accountLocked:true, accountExpired:false, enabled:true).save()
            def registrationCode = new RegistrationCode(username:user.username).save()

        when:"that user engages the verify registration action with their registration code's token"
            RestResponse resp = rest.get("http://localhost:${serverPort}/register/verifyRegistration?t=${registrationCode.token}")

        then:"that user account should be unlocked, not expired, and enabled"
            resp.status == 200
            def updatedUser = User.findByUsername(user.username)
            updatedUser.accountLocked == false
            updatedUser.accountExpired == false
            updatedUser.enabled == true
    }
}
