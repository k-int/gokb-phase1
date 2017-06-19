

# Initial Setup

## Database

Default commands to set up pgsql for gokb follow:

    sudo -u postgres psql postgres


    psql (9.5.4)
    Type "help" for help.

    postgres=# CREATE DATABASE gokb;
    CREATE DATABASE
    postgres=# CREATE USER knowint WITH PASSWORD 'knowint';
    CREATE ROLE
    postgres=# GRANT ALL PRIVILEGES ON DATABASE gokb to knowint;
    GRANT
    \q


Connect with

psql -h localhost -U knowint gokb

## Config

mkdir ~/.grails/gokb-config.groovy


With contents --LIKE-- ::

kuali.analytics.code="xxx"
grails.plugins.springsecurity.ui.forgotPassword.emailFrom='user@host'
grails.plugins.springsecurity.ui.register.emailFrom = 'user@host'
grails.plugins.springsecurity.ui.forgotPassword.emailSubject = 'GoKB Forgotten Password'
gokb.theme='spacelab'  // spacelab,cosmo,cyborg,yeti
gokb.es.cluster='elasticsearch'
serverUrl='https://host/gokb'

grails {
  mail {
    host = "smtp.gmail.com"
    port = 465
    username = "username"
    password = "password"
    props = ["mail.smtp.auth":"false",
             "mail.smtp.socketFactory.port":"465",
             "mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
             "mail.smtp.socketFactory.fallback":"false"]
  }
}

searchApi = [
  'path'        : '/es/',
  'indices'     : 'gokb',
  'types'       : 'component',
  'typingField' : 'componentType',
  'port'        : 443
]

newDash=false
decisionSupport=true



# Testing

In order to test in an environment comparable to deployment, gokb uses postgres as it's test database. Use the procedures in InitialSetup above to also confiure an gokb database called gokbtest.
Note that the test configuration for integration and functional tests are create-drop meaning that the DB is dropped after the tests have run.

Devs should NOT run "grails test-app" as this seems to create conflicts in some systems, instead test separately:
* grails test-app unit:
* grails test-app integration:
* grails test-app functional:
