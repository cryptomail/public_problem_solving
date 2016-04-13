
var Promise = require('promise');
var request = require('request');

/*
Global functions area for the util module
*/

function getErrorObject(){
/*
  Very silly but useful hack to extract the time and place of the error for easy peasy line and location or error tracking :)
*/
    try { throw Error('') } catch(err) { return err; }
}


/*
Extracting what we need from an error at the time of error assertion / emition
*/

function lineNumberAndFile() {
  var err = getErrorObject();
  var caller_line = err.stack.split("\n")[5];
  var index = caller_line.indexOf("at ");
  var clean = caller_line.slice(index+2, caller_line.length);

  return caller_line;
}

/*
Main module here
*/
module.exports = function(baseURL, secretToken) {
 

  return {
  	secretToken: secretToken,
    baseURL: baseURL,
    channels: undefined,
    FilesErrorStates: {
      'NOT_AUTHED' : 'not_authed',
      'INVALID_CHANNEL' : 'invalid_channel',
      'INVALID_AUTH' : 'invalid_auth',
      'ACCOUNT_INACTIVE' : 'account_inactive',
      'INVALID_ARRAY_ARG' : 'invalid_array_arg',
      'INVALID_CHARSET' : 'invalid_charset',
      'INVALID_FORM_DATA' : 'invalid_form_data',
      'INVALID_POST_TYPE' : 'invalid_post_type',
      'MISSING_POST_TYPE' : 'missing_post_type',
      'REQUEST_TIMEOUT' : 'request_timeout',
      'NO_FILE' : 'no_file',
    },

    /*
      Use the console but emit error / warn / debug candy
    */
  	errorlog: function() {
      var d = new Date();
      console.log(d, '*****ERROR' + lineNumberAndFile(), arguments)
    },
    log: function() {
      var d = new Date();

      console.log(d, '*****DEBUG' + lineNumberAndFile(), arguments)
    },

  constructThumbFileNamePostfix: function(fname, separator, thumbsize, ftype) {
    var retval = fname + separator + thumbsize + '.' + ftype;
    return retval.toLowerCase();
  },
    /*
    Issue a simple GET request on slack api: Object.Command
    Assumes baseURL is here, along with the secret token.
    Extra safety employed on construction of URL to ensure no hanky panky
    */
    issueSimpleGETRequest: function(object, command) {
      var self = this;
      var p = new Promise(function(resolve,reject) {

        
        var commandPart = self.baseURL + object + '.' + command;
        var URL = commandPart + '?' + 'token=' + encodeURIComponent(self.secretToken);

        /*
        Snap the main request
        */
        request.get(URL,function (err, res, body) {
          if(err) {
            reject('Something bad happened at POST URL ' + commandPart);
            return;
          }
          if(res.statusCode !== 200) {
          
            reject('HTTP response: Something bad happened at GET URL ' + commandPart); // Do not log the token info :)
            return;
          }
                
          var res = JSON.parse(body);

          resolve(res);

        });
      });

      return p;
      
    },

    issueSimplePOSTRequest: function(object, command, formData) {
      var self = this;
      var p = new Promise(function(resolve,reject) {

        var commandPart = self.baseURL + object + '.' + command;
        var URL = commandPart; //For consistency upstairs on GET.

        /*
        Snap the main request
        */
        
        request.post(URL,{formData:formData}, function (err, res, body) {
          if(err) {
            reject('Something bad happened at POST URL ' + commandPart);
            return;
          }
          if(res.statusCode !== 200) {
          
            reject('HTTP response: Something bad happened at POST URL ' + commandPart); // Do not log the token info :)
            return;
          }
                
          var res = JSON.parse(body);

          if(!res || !res.ok) {
            reject(res);
            return;
          }
          resolve(res.file);

        });
      });

      return p;
      
    },

    /*
    getChannel:
    will employ getChannels which caches all the channels available
    Linear search :_( eh cardinality < 1024?!  One thing to test in the next test library I make :)
    */
    getChannel: function(channelName) {
      var self = this;
      var p = new Promise(function(resolve,reject) {

        self.getChannels(false).then(function success(channels) {

          if(!channels || !channelName || channelName.length === 0) {
            reject('getChannel failed: ' + channelName);
            return;
          }

          var x,n;
          n = channels.length;

          for(x=0; x < n; x++) {
            if(channels[x].name && channels[x].name.toLowerCase() === channelName.toLowerCase()) {
              resolve(channels[x]);
              return;
            }
          }
          reject('Cannot find channel ' + channelName);
        },
        function error(e) {
          reject(e);
        })
      });

      return p;
    },


    getChannels: function(forceRefresh) {
      var self = this;
      var p = new Promise(function(resolve,reject) {
        if((!self.channels) || forceRefresh) {
          self.issueSimpleGETRequest('channels','list').then(
          function success(data) {
            if(!data.ok) {
              reject('Data is not ok');
              return;
            }
            self.channels = data.channels;
            resolve(data.channels);
          },
          function error(e) {
            reject(e);
          });
        } else {
          resolve(self.channels);
        }
        
      });
      return p;
    }
  };
};