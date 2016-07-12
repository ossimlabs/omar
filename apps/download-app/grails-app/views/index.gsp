<!doctype html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Welcome to Grails</title>
        <style type="text/css" media="screen">
            #status {
                background-color: #eee;
                border: .2em solid #fff;
                margin: 2em 2em 1em;
                padding: 1em;
                width: 12em;
                float: left;
                -moz-box-shadow: 0px 0px 1.25em #ccc;
                -webkit-box-shadow: 0px 0px 1.25em #ccc;
                box-shadow: 0px 0px 1.25em #ccc;
                -moz-border-radius: 0.6em;
                -webkit-border-radius: 0.6em;
                border-radius: 0.6em;
            }

            #status ul {
                font-size: 0.9em;
                list-style-type: none;
                margin-bottom: 0.6em;
                padding: 0;
            }

            #status li {
                line-height: 1.3;
            }

            #status h1 {
                text-transform: uppercase;
                font-size: 1.1em;
                margin: 0 0 0.3em;
            }

            #page-body {
                margin: 2em 1em 1.25em 18em;
            }

            h2 {
                margin-top: 1em;
                margin-bottom: 0.3em;
                font-size: 1em;
            }

            p {
                line-height: 1.5;
                margin: 0.25em 0;
            }

            #controller-list ul {
                list-style-position: inside;
            }

            #controller-list li {
                line-height: 1.3;
                list-style-position: inside;
                margin: 0.25em 0;
            }

            @media screen and (max-width: 480px) {
                #status {
                    display: none;
                }

                #page-body {
                    margin: 0 1em 1em;
                }

                #page-body h1 {
                    margin-top: 0;
                }
            }
        </style>
<script src="https://code.jquery.com/jquery-3.0.0.min.js"></script>
<script src="http://jqueryfiledownload.apphb.com/Scripts/jquery.fileDownload.js"></script>
    </head>
    <body>
        <a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div id="status" role="complementary">
            <h1>Test Download</h1>
            <ul>
                <li>Environment: ${grails.util.Environment.current.name}</li>
                <li>App profile: ${grailsApplication.config.grails?.profile}</li>
                <li>App version: <g:meta name="info.app.version"/></li>
                <li>Grails version: <g:meta name="info.app.grailsVersion"/></li>
                <li>Groovy version: ${GroovySystem.getVersion()}</li>
                <li>JVM version: ${System.getProperty('java.version')}</li>
                <li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
            </ul>
            <h1>Artefacts</h1>
            <ul>
                <li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
                <li>Domains: ${grailsApplication.domainClasses.size()}</li>
                <li>Services: ${grailsApplication.serviceClasses.size()}</li>
                <li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
            </ul>
            <h1>Installed Plugins</h1>
            <ul>
                <g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
                    <li>${plugin.name} - ${plugin.version}</li>
                </g:each>
            </ul>
        </div>
        <div id="page-body" role="main">
            <h1>Welcome to Grails</h1>
            <p>Congratulations, you have successfully started your first Grails application! At the moment
               this is the default page, feel free to modify it to either redirect to a controller or display whatever
               content you may choose. Below is a list of controllers that are currently deployed in this application,
               click on each to execute its default action:</p>

            <div id="controller-list" role="navigation">
                <h2>Available Controllers:</h2>
                <ul>
                    <g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
                        <li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
                    </g:each>
                </ul>
            </div>
        </div>
        <div>
         <button type="button" onClick="javascript:download()">Click Me!</button>
        </div>
<g:javascript>

function download()
{
    var fileName = window.prompt("Enter File Name","omar.zip")

    var postBody =
    {
        "type":"Download",
        "zipFileName": fileName,
        "archiveOptions":
        {
            "type": "zip"
        },
        "fileGroups":
        [
            {
                "rootDirectory":"",
                "files":["/Users/nroberts/Projects/data/cadrg/rpf__cadrg_1060889977_67001/a.toc","/Users/nroberts/Projects/data/cadrg/rpf__cadrg_1060889977_67001/ctpc"]
            }
        ]
    };

  $.fileDownload("/archive/download",{
         httpMethod: "POST",
         dataType: "text",
         contentType: "plain/text",
         data:{fileInfo:JSON.stringify(postBody)}
    });

/*
$.ajax({
  type: "POST",
  url: "/archive/download",
  data:JSON.stringify(postBody),
  contentType: 'application/json; charset=UTF-8',
  success: function(){alert("HeckYea")},
  dataType: "json"
});
*/

}

</g:javascript>

    </body>

</html>
