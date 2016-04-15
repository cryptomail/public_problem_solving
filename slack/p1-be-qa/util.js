
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
    debug_get_requests:false,
  	secretToken: secretToken,
    specialSnowflakeCount: 0, /* Slack has a bug where meta data may be incorrect! */
    baseURL: baseURL,
    channels: undefined,
    Limits: {
      'FILE_TITLE_LENGTH': 255,
      'MAX_FILES_IN_WINDOW' : 1000
    },
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
      'FILE_DELETED' : 'file_deleted',
      'FILE_NOT_FOUND' : 'file_not_found',
      'NO_FILE' : 'no_file',
    },
    FilesFileTypes: {
      'IMAGES' : 'images'
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

    queryForAdditionalParams: function(map, isFirst) {

      var queryString = '';
      for (var key in map) {
        if (key && map.hasOwnProperty(key) && map[key] !== null) {
          queryString += (!isFirst ? '&' : '')  + encodeURIComponent(key) + '=' + encodeURIComponent(map[key]);
          isFirst = false;
        }
      }

      return queryString;
    },
    /*
    Issue a simple GET request on slack api: Object.Command
    Assumes baseURL is here, along with the secret token.
    Extra safety employed on construction of URL to ensure no hanky panky
    */
    issueSimpleGETRequest: function(object, command, map) {
      var self = this;

      var p = new Promise(function(resolve,reject) {

        var URL;
        var commandPart = self.baseURL + object + '.' + command;
        var isFirst = true;
        if(self.secretToken) {
          URL =  commandPart + '?' + 'token=' + encodeURIComponent(self.secretToken);
          isFirst = false;
        } else {
          URL = commandPart + '?';
        }
        
        var additionalParams;
        if(map) {

          additionalParams = self.queryForAdditionalParams(map,/* prepend the first & if nothing before this */isFirst);
          if(additionalParams) {
            URL += additionalParams;
          }
          
        } 
        if(self.debug_get_requests) {
          self.log(URL);
        }
        
        /*
        Snap the main request
        */
        request.get(URL,function (err, res, body) {
          if(err) {

            reject(err);
            return;
          }
          if(res.statusCode !== 200) {
           
            reject('HTTP response: Something bad happened at GET URL ' + commandPart + ' res.statusCode: ' + res.statusCode); // Do not log the token info :)
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
            reject(err);
            return;
          }
          if(res.statusCode !== 200) {
          
            reject(res); // Do not log the token info :)
            return;
          }
                
          var response = JSON.parse(body);

          if(!response || !response.ok) {
            reject(response);
            return;
          }
          resolve(response.file);

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


    getFileCount: function(user, channel, types) {
      var self = this;
      var p = new Promise(function(resolve,reject) {
        var something = false;
        var params = {};
        if(user) {
          something = true;
          params.user = user;
        }
        if(channel) {
          something = true;
          params.channel = channel;
        }
        if(types) {
          something = true;
          params.types = types;
        }
        if(!something) {
          params = null;
        }
        self.issueSimpleGETRequest('files','list',params).then(
        function success(data) {
          if(!data.ok) {
            reject(data);
            return;
          }
          resolve(data.paging.total);
        },
        function error(e) {
          reject(e);
        });
       
        
      });
      return p;
    },

    getFiles: function(user,channel,types,count,page) {
      var self = this;
      var p = new Promise(function(resolve,reject) {
        
          var iterations = Math.floor(count / self.Limits.MAX_FILES_IN_WINDOW) + 1;
          var x;
          var waitFor = [];
          var allFiles = [];
        
          
          for(x=0; x < 1; x++ ) {
            var mapParams={user:user, channel:channel, types:types, page:page, count:count};

            var newP = self.issueSimpleGETRequest('files','list',mapParams);
            
            waitFor.push(newP);

            Promise.all(waitFor).then(function success(promiseArray) {
              var y = 0;
              for(y = 0; y < promiseArray.length; y++) {
                var z = 0;
                for(z=0; z < promiseArray[y].files.length; z++) {
                  allFiles.push(promiseArray[y].files[z]);
                  
                }
              }

              resolve(allFiles);
            },
            function error(e) {
              self.errorlog('getFiles failed: ', e);
              reject(e);
            })
          }
      });
      return p;
    },
    getAllFiles: function(user,channel,types) {
      var self = this;
      var p = new Promise(function(resolve,reject) {
        
        self.getFileCount(user,channel,types).then(
        function success(count) {

          var iterations = Math.floor(count / self.Limits.MAX_FILES_IN_WINDOW) + 1;
          var x;
          var waitFor = [];
          var allFiles = [];
        
          
          for(x=0; x < iterations; x++ ) {
            var mapParams={user:user, channel:channel, types:types, page:x+1, count:self.Limits.MAX_FILES_IN_WINDOW};

            var newP = self.issueSimpleGETRequest('files','list',mapParams);
            
            waitFor.push(newP);

            Promise.all(waitFor).then(function success(promiseArray) {
              var y = 0;
              for(y = 0; y < promiseArray.length; y++) {
                var z = 0;
                for(z=0; z < promiseArray[y].files.length; z++) {
                  allFiles.push(promiseArray[y].files[z]);
                  
                }
              }

              resolve(allFiles);
            },
            function error(e) {
              self.errorlog('getAllFiles failed: ', e);
              reject(e);
            })
          }
          
        },
        function error(e) {

          reject(e);
        });
        
      });
      return p;
    },
    getChannels: function(forceRefresh) {
      var self = this;
      var p = new Promise(function(resolve,reject) {
        if((!self.channels) || forceRefresh) {
          self.issueSimpleGETRequest('channels','list',null).then(
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
    },

    deleteFile: function(fileId) {
      var self = this;
      var p = new Promise(function(resolve,reject) {
        var something = false;
        var params = {};
        params.file = fileId;
        
        self.issueSimpleGETRequest('files','delete',params).then(
        function success(data) {
          
          if(!data) {
            data = {};
            data.ok = false;
            data.error = 'UNKNOWN';
          }
          if(!data.ok) {
            data.file = fileId;
            if(data.error === self.FilesErrorStates.FILE_DELETED) {
              resolve(data);
              return; 
            }
            //self.log('DEBUG: delete file may have failed  ' + fileId);
            reject(data);
            return;
          }
          //self.log('DEBUG: deleted file ' + fileId);
          resolve(data.ok);
        },
        function error(e) {
          //self.log('WARN: delete file may have failed ' + fileId);
          e.file = fileId;
          reject(e);
        });
       
      });
      return p;
    },
    blabber: function(something, ntimes, separtor) {
      var n = ntimes;

      var output = '';
      while(n > 0) {
        output += something;
        n--;
        if(separtor && n) {
          output += separtor;
        }
      }

      return output;
    }
  };
};