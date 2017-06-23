//demo application values
var msisdn;
var clientID;
var clientSecret;
var discoveryURL;
var redirectURL;
var xRedirect;
var xSourceIp = '';

var mobileConnectUrl = '/api/mobileconnect';
var parametersURL = '/api/mobileconnect/get_parameters';
var discoveryUrl = '/api/mobileconnect/start_discovery';
var endpointsURL = '/api/mobileconnect/endpoints';
var authenticationUrl = '/api/mobileconnect/start_authentication';
var authorizationUrl = '/api/mobileconnect/start_authorization';
var userInfoUrl = '/api/mobileconnect/user_info';
var identityUrl = '/api/mobileconnect/identity';
var manualDiscoveryUrl = '/api/mobileconnect/start_manual_discovery';
var noProviderMetadataUrl = '/api/mobileconnect/start_manual_discovery_no_metadata';
var authorizationR1 = '/api/mobileconnect/start_authentication_r1';

function openRequestOptionsMenu() {
    if ($('#requestOptionsMore').css('visibility') === 'hidden') {
        $("#requestOptionsMore").css({"visibility": "visible"})
    } else {
        $("#requestOptionsMore").css({"visibility": "hidden"})
    }
}

function openRequestParametersMenu() {
    if ($('#requestParameters').css('visibility') === 'hidden') {
        $('#requestParameters').css({"visibility": "visible"})
    } else {
        $('#requestParameters').css({"visibility": "hidden"})
    }
}

var getRequestParameters = function getRequestParameters() {
    clientID = $("#clientID").val();
    clientSecret = $("#clientSecret").val();
    discoveryURL = $("#discoveryURL").val();
    redirectURL = $("#redirectURL").val();
    xRedirect = $("#xRedirect").val();

    var queryString = '?clientID=' + encodeURIComponent(clientID) +
        '&clientSecret=' + encodeURIComponent(clientSecret) +
        '&discoveryURL=' + encodeURIComponent(discoveryURL) +
        '&redirectURL=' + encodeURIComponent(redirectURL) +
        '&xRedirect=' + encodeURIComponent(xRedirect) + '&scope=' + 'openid mc_' + $('input[name=authType]:checked').val() +
        encodeURIComponent(getPermissionsRequested());

    $.get(parametersURL + queryString, function (data) {
        var input = $('#msisdn');
        var msisdn = '';
        if ($('#msisdn-toogle').is(':checked')) {
            msisdn = input.val();
        }
        api.discovery(msisdn, xSourceIp["ip"]);
    });

};

var getRequestParametersWithoutDiscovery = function getRequestParametersWithoutDiscovery() {
    var input = $('#clientName');
    var clientName = '';
    var providerMetadata = false;
    if ($('#metadataURL').is(":visible")) {
        clientName = input.val();
        providerMetadata = true;
    }
    var clientId = $('#clientIDWD').val();
    var clientSecret = $('#clientSecretWD').val();
    var subId = $('#subscriberID').val();

    //endpoints
    var auth = $('#authURL').val();
    var token = $('#tokenURL').val();
    var userInfo = $('#userInfoURL').val();
    var metadata = $('#metadataURL').val();
    var discovery = $("#discoveryURLWD").val();
    var redirect = $("#redirectURLWD").val();

    var queryString = '?';
    queryString += 'authURL=' + auth + '&tokenURL=' + token + '&userInfoURl=' + userInfo +
        '&discoveryURL=' + discovery + '&redirectURL=' + redirect;

    if (providerMetadata) {
        queryString += '&metadata=' + metadata;
    }
    $.get(endpointsURL + queryString, function (data) {
        api.manualDiscovery(subId, clientId, clientName, clientSecret, providerMetadata);
    });

};

function setIpAddress() {
    $('#xSourceIP').change(function () {
        if ($(this).is(':checked')) {
            $.getJSON('//ipapi.co/json/', function (data) {
                xSourceIp = JSON.stringify(data, null, 2);
                xSourceIp = JSON.parse(xSourceIp);
            });
        } else {
            xSourceIp = '';
        }
    });
}

function start() {
    hide('.error')
    show('#redirect-url');
    hide('#logged-in');
    hide('#user-info-button');
    hide('#identity-button');
    hide('#user-info');
    hide('#identity');
    $("#information").hide("slow", function () {
        $("#redirect-url").show("slow");
    });

    getRequestParameters: getRequestParameters();
}

function startAuthz() {
    hide('.error')
    show('#redirect-url');
    hide('#logged-in');
    $("#information").hide("slow", function () {
        $("#redirect-url").show("slow");
    });

    getRequestParametersWithoutDiscovery: getRequestParametersWithoutDiscovery();
}

var openWindow = function openWindow(url) {
    $('#redirect-url').attr('src', url);
};

var hide = function hide(selector) {
    var el = $(selector);
    el.addClass('hidden');
    return el;
};

var show = function show(selector) {
    var el = $(selector);
    el.removeClass('hidden');
    return el;
};

var setContent = function setContent(selector, content) {
    var el = $(selector);
    el.html(content);
    return el;
};

var generateParams = function generateParams(params) {
    var query = '';
    var first = true;
    for (var key in params) {
        if (params.hasOwnProperty(key) && params[key]) {
            query = query + (first ? '' : '&') + key + '=' + params[key];
            first = false;
        }
    }

    return query;
};

var getPermissionsRequested = function getPermissionsRequested() {
    var scope = '';
    $('.permissions-check input').each(function (i, el) {
        var $el = $(el);
        if ($el.prop('checked')) {
            scope = scope + ' ' + $el.attr('data-scope');
        }
    });
    return scope;
};

var getAuthScope = function getAuthScope() {
    var scope = 'mc_';
    if ($('input[name=authType]:checked').val() === undefined) {
        scope = '';
        return scope;
    } else {
        scope = scope + $('input[name=authType]:checked').val();
        scope = scope + getPermissionsRequested();
    }

    return scope;
};

var clearAttempt = function clearAttempt() {
    api.currentAttempt = {};
    closeChildWindow();
};

var closeChildWindow = function closeChildWindow() {
    if (window.child) {
        window.child.close();
    }
};

var hideLogin = function hideLogin() {
    hide('#redirect-url');
    hide('#pre-login');
    show('#logged-in');
    if ($('#value-access-token').text() !== undefined) {
        show('#user-info-button')
        show('#identity-button')
    }
};

var printTokenInfo = function printTokenInfo(token) {
    $('#value-access-token').html(token.access_token);
    $('#value-id-token').html(token.id_token);
    $('#value-time-received').html(token.time_received);
};

var printInfo = function printInfo(response) {
    $('#value-application-name').html(api.currentAttempt.clientName);
    printTokenInfo(response.token);
    if (response.tokenValidated) {
        $('#value-token-validated').html("Token Validated");
    } else {
        $('#value-token-validated').html("Token Not Validated");
    }
    if (response.identity) {
        setContent('#value-identity-prefetched', JSON.stringify(response.identity, null, 2));
    }
};

var printTokenInfo = function printTokenInfo(token) {
    show('#token-table');
    show('#value-access-token');
    show('#value-id-token');
    show('#value-time-received');
    hide('.error');
    $('#value-access-token').html(token.access_token);
    $('#value-id-token').html(token.id_token);
    $('#value-time-received').html(token.time_received);
};

var printInfo = function printInfo(response) {
    show('#value-application-name');
    $('#value-application-name').html(api.currentAttempt.applicationShortName);
    printTokenInfo(response.token);
    if (response.identity) {
        setContent('#value-identity-prefetched', JSON.stringify(response.identity, null, 2));
    }
};
var newSDKSession = "";

var api = {
    demoapp: true,
    currentAttempt: {},
    httpCallback: function handleResponse(data) {
        console.log(data);
        if (data["status"] === 'failure') {
            api.error(data);
        }
        api[data.action](data);
    },
    httpCallbackTest: function handleResponse(data) {
    },
    urls: function sendUrls(authURL, tokenURL, userInfoURL, metadataURL, discoveryURL, redirectURL, providerMetadata) {
        var queryString = '?';
        queryString += 'authURL=' + authURL + '&tokenURL=' + tokenURL + '&userInfoURl=' + userInfoURL +
            '&discoveryURL=' + discoveryURL + '&redirectURL=' + redirectURL;

        if (providerMetadata) {
            queryString += '&metadata=' + metadataURL;
        }
        $.get(endpointsURL + queryString, api.httpCallback);
    },
    parameters: function sendParameters(clientID, clientSecret, discoveryURL, redirectURL, xRedirect) {
        var queryString = '?';
        queryString = 'clientID=' + clientID + '&clientSecret=' + clientSecret + '&discoveryURL=' +
            discoveryURL + '&redirectURL=' + redirectURL + '&xRedirect=' + xRedirect;

        $.get(parametersURL + queryString);
    },
    manualDiscovery: function discovery(subId, clientId, clientName, clientSecret, providerMetadata) {
        var queryString = '?';

        if (typeof subId === "string") {
            queryString += ('subId=' + encodeURIComponent(subId) + "&");
        }

        if (typeof clientId === "string") {
            queryString += ('clientId=' + encodeURIComponent(clientId) + "&");
        }

        if (typeof clientName === "string" && providerMetadata) {
            queryString += ('clientName=' + encodeURIComponent(clientName) + "&");
        }

        if (typeof clientSecret === "string") {
            queryString += ('clientSecret=' + encodeURIComponent(clientSecret) + "&");
        }

        if (providerMetadata) {
            $.get(manualDiscoveryUrl + queryString, api.httpCallback);
        }
        else {
            $.get(noProviderMetadataUrl + queryString, api.httpCallback);
        }
    },
    discovery: function discovery(response, sourceIp) {
        var queryString = '';

        if (typeof response === "string") {
            queryString += '?msisdn=' + encodeURIComponent(response);
            if (sourceIp !== undefined) {
                queryString += '&sourceIp=' + encodeURIComponent(sourceIp);
            }
        }

        $.get(discoveryUrl + queryString, api.httpCallback);
    },
    start_authentication: function start_authentication(response) {
        var queryString = '?' + generateParams({
                subscriberId: response.subscriberId,
                sdkSession: response.sdkSession,
                scope: getAuthScope()
            });

        api.currentAttempt.session = response.sdkSession;
        newSDKSession = response.sdkSession;
        if (typeof response.applicationShortName !== 'undefined') {
            api.currentAttempt.applicationShortName = response.applicationShortName;
        }

        if ($('input[name=authType]:checked').val() === 'authn') {
            var url = authenticationUrl;
        } else {
            var url = authorizationUrl;
        }
        if (!api.demoapp && !api.providerMetadata) {
            url = authorizationR1;
        }

        $.get(url + queryString, api.httpCallback);

        if (api.currentAttempt.session !== 'undefined') {
            $('user-info-button').show('slow');
            $('identity-button').show('slow');
        } else {
            $('user-info-button').hide('slow');
            $('identity-button').hide('slow');
        }
    },
    authentication: function authentication(response) {
        api.currentAttempt.expectedState = response.state;
        api.currentAttempt.expectedNonce = response.nonce;
        api.currentAttempt.session = response.session;
        openWindow(response.url);
    },
    operator_selection: function operator_selection(response) {
        openWindow(response.url);
    },
    handle_redirect: function handle_redirect(url) {
        var queryString = '&' + generateParams({
                expectedState: api.currentAttempt.expectedState,
                expectedNonce: api.currentAttempt.expectedNonce,
                sdkSession: newSDKSession
            });

        $.get(url + queryString, api.httpCallback);
    },
    complete: function complete(response) {
        if (response.token) {
            closeChildWindow();
            api.currentAttempt.token = response.token;
            hideLogin();
            printInfo(response);
        } else if (response.outcome) {
            api.operation_outcome({description: response.outcome});
        } else {
            api.error({description: 'Authentication not returned, user not logged in'});
        }
    },
    request_user_info: function request_user_info() {
        var queryString = '?' + generateParams({
                sdkSession: newSDKSession,
                accessToken: api.currentAttempt.token.access_token,
            });

        $.get(userInfoUrl + queryString, api.httpCallback);
    },
    user_info: function user_info(response) {
        setContent('#value-user', JSON.stringify(response.identity, null, 2));
        show('#value-user');
        show('#user-info');
    },
    request_identity: function request_identity() {
        var queryString = '?' + generateParams({
                sdkSession: newSDKSession,
                accessToken: api.currentAttempt.token.access_token,
            });

        $.get(identityUrl + queryString, api.httpCallback);
    },
    identity: function identity(response) {
        setContent('#value-identity', JSON.stringify(response.identity, null, 2));
        show('#identity');
    },
    error: function error(response) {
        clearAttempt();

        $('.error').html(response.description);
    },
    operation_outcome: function operation_outcome(response) {
        clearAttempt();
        $('#refresh-revoke-outcome').html(response.description);
    }
};


$(document).ready(function ($) {
    $('#xRedirect').chec
    $.getJSON("data/defaultData.json", function (data) {
        $('#msisdn').val(data["msisdn"]);
        $('#clientID').val(data["clientID"]);
        $('#clientSecret').val(data["clientSecret"]);
        $('#discoveryURL').val(data["discoveryURL"]);
        $('#redirectURL').val(data["redirectURL"]);
        if (data["xRedirect"] === "True") {
            $('#xRedirect').val(data["xRedirect"]);
            $('#xRedirect').prop("checked", true);
        } else {
            $('#xRedirect').val(false);
            $('#xRedirect').prop("checked", false);
        }
    });

    $.getJSON("data/defaultDataWD.json", function (data) {
        $('#clientIDWD').val(data["clientID"]);
        $('#clientSecretWD').val(data["clientSecret"]);
        $('#subscriberID').val(data["subscriberID"]);
        $('#clientName').val(data["clientName"]);
        $('#authURL').val(data["authorizationURL"]);
        $('#tokenURL').val(data["tokenURL"]);
        $('#userInfoURL').val(data["userInfoURL"]);
        $('#metadataURL').val(data["metadataURL"]);
        $('#discoveryURLWD').val(data["discoveryURL"]);
        $('#redirectURLWD').val(data["redirectURL"]);

    });

    $('#msisdn-toogle').change(function () {
        if ($(this).is(':checked')) {
            $("#msisdn").show('slow');
        } else {
            $("#msisdn").hide('slow');
        }
    });
    $('#with-metadata').change(function () {
        if ($(this).is(':checked')) {
            api.providerMetadata = true;
            $("#clientName").show("slow");
            $("#clientNameText").show("slow");
            $("#textMetadata").show("slow");
            $("#metadataURL").show("slow");
        } else {
            $("#clientName").hide("slow");
            $("#clientNameText").hide("slow");
            $("#metadataURL").hide("slow");
            $("#textMetadata").hide("slow");
        }
    });

    $('#user-info-button').click(function () {
        api.request_user_info();
    });

    $('#identity-button').click(function () {
        api.request_identity();
    });

    $(function () {
        enableUserInfo();
        $(".identity-info").click(enableUserInfo);
    });

    $(function () {
        enableIdentity();
        $(".user-info").click(enableIdentity);
    });

    function enableUserInfo() {
        if (this.checked) {
            $(".user-info").attr("disabled", true);
        } else {
            $(".user-info").removeAttr("disabled");
        }
    }

    function enableIdentity() {
        if (this.checked) {
            $(".identity-info").attr("disabled", true);
        } else {
            $(".identity-info").removeAttr("disabled");
        }
    }
});