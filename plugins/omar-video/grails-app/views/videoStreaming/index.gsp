<html xmlns:asset="http://www.w3.org/1999/html">
<head>
    <meta name="layout" content="main"/>
    <asset:stylesheet src="videoStreaming.css"/>
</head>
<body>
<div class="nav">
    <ul>
        <li>
            <g:link uri="/" class="home">Home</g:link>
        </li>
    </ul>
</div>
<div class="content">
    <h1>Video</h1>
    <div id="myvid">
        <div data-ratio="0.6" data-autoplay='true' class="flowplayer">
            <video data-title="FLV video">
                <source type="video/flash" src="${videoURL}">
            </video>
        </div>
    </div>
</div>
<asset:javascript src="videoStreaming.js"/>
<asset:script>
    $(document).ready(function() {
    });
</asset:script>
<asset:deferredScripts/>
</body>
</html>