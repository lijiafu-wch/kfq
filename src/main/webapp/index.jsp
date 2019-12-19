<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>分期</title>
    <meta name="decorator" content="default"/>
    <script src="http://code.jquery.com/jquery-latest.js"></script>
    <script type="text/javascript">
        $(document).ready(function() {
            alert(123);
            $("#inputForm").submit();
        });
    </script>
</head>
<body class="hideScroll">
<form  id="inputForm"   action="${url}" method="post"  >
    <input type="hidden" name="sign" value="${sign}"/>
    <input type="hidden" name="midPlatform" value="${midPlatform}"/>
    <input type="hidden" name="version" value="${version}"/>
    <input type="hidden" name="data" value="${data}"/>
</form>
</body>

</html>