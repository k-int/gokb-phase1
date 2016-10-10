

~/.groovy/grapeConfig.xml used for this script::

    <?xml version="1.0"?>
    <ivysettings>
        <settings defaultResolver="downloadGrapes"/>
        <resolvers>
            <chain name="downloadGrapes">
                <!-- todo add 'endorsed groovy extensions' resolver here -->
                <ibiblio name="central" root="http://central.maven.org/maven2/" m2compatible="true"/>
                <ibiblio name="local" root="file:${user.home}/.m2/repository/" m2compatible="true"/>
                <filesystem name="cachedGrapes">
                    <ivy pattern="${user.home}/.groovy/grapes/[organisation]/[module]/ivy-[revision].xml"/>
                    <artifact pattern="${user.home}/.groovy/grapes/[organisation]/[module]/[type]s/[artifact]-[revision].[ext]"/>
                </filesystem>
                <ibiblio name="codehaus" root="http://repository.codehaus.org/" m2compatible="true"/>
                <ibiblio name="ibiblio" m2compatible="true"/>
                <ibiblio name="java.net2" root="http://download.java.net/maven/2/" m2compatible="true"/>
                <ibiblio name="k-int" root="http://nexus.k-int.com/content/repositories/releases" m2compatible="true"/>
            </chain>
        </resolvers>
    </ivysettings>

