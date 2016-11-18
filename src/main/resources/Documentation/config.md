CloudWatch Metrics Reporter Configuration
=========================================

File `@PLUGIN@.config`
-------------------------

The optional file `$site_path/etc/@PLUGIN@.config` is a Git-style
config file that controls the settings for the @PLUGIN@ plugin.

cloudwatch.host
:	Hostname of the CloudWatch server. Defaults to `localhost`.

cloudwatch.port
:	Port number of the CloudWatch server. Defaults to `2003`.

cloudwatch.namespace
:	Namespace to use when reporting metrics. Defaults to `gerrit.`
	suffixed with the hostname of `localhost`.
