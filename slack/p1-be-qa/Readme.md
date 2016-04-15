#Black Box Slack API Testing
![alt text](https://raw.githubusercontent.com/cryptomail/public_problem_solving/master/slack/p1-be-qa/images/screenshot.png "Black Box Slack API Testing")

##This test library requires Mocha.  Mocha installation is very easy and can be found here: https://mochajs.org/#installation

###For the impatient, download source into current working dir:
```bash
$ npm install -g mocha
$ npm install
$ export SLACK_SECRET_TOKEN=xxxx-xxxxxxxxx-xxxx # I will never EVER put this in a configuration file in (D)RCS.  EVER :D.
$ mocha
```


####Breakdown of tests and testing tactics/classes and notes!

####Particularly, *NOT* covered in this suite:
1. Scalability
2. Performance
3. Wholesale bulk testing / throughput


####Mildly covered in this suite:
1. Field limits testing/fuzzing

Upon request, I will perform additional tests to suffce the above three items, or describe a plan that can suffice them.

####Covered:

####Files.Upload
1. Positive tests 
  * ~~Simple positive test~~ 04/12/2016 
  * ~~unknown entity fields~~ Determination:  Slack API is friendly in that ignores unknown elements: 
  ```javascript
  	var formData = {
							muppets: 'animal, kermit, miss piggy',
							file: {
								value:  fs.createReadStream(fnamePath),
								options: {
									filename: fname,
									contentType: 'image/png'
								}
							}	
						};
```
  * ~~Simple unknown field in payload test: muppets in form!~~ 04/12/2016 
  * ~~unknown content resolves to correct content~~ 04/13/2016 Real PDF upload respresented as kermitthefrog content type.
  * ~~unset file type resolves to correct content~~ 04/13/2016 Lied to using .png extension but magic is PDF! :) worked and read magic properly.
  * ~~send to channel~~ 04/13/2016 
  * ~~channels: same channel twice~~ 04/13/2016 
2. Negative tests
  * ~~Invalid auth~~04/13/2016
  * ~~No auth provided~~ 04/13/2016
  * ~~No content, empty file~~ 04/13/2016 NOT DOCUMENTED ON SLACK!!! Filed under "OTHER" in documentation easy out.
  * ~~misnomed content~~ 04/13/2016 Real PDF upload respresented as PNG
  * ~~send to invalid channel~~ 04/13/2016 

3. Super evil tests
  * ~~bust a field limit: filename~~: 04/13/2016:  *Recommendation*: Do not let anyone know you're using AWS.  The less info people have about internals, the better.  One day we may move off of it, if you read the literature :) 
  * ~~blank channels with all kinds of crap in it 100,000 commas~~ 04/13/2016
  * ~~blank channels with no content but space~~ 04/13/2016
  * ~~channels: Send a lot of channels of same name~~ 4/13/2016 Notes: This should be successful.  Likely an internal API is not doing something correctly.  I will fix it. :)
  

####Files.list 
1. Simple positive tests
  * ~~Can get a count of all the images~~ 04/13/2016: Gets first file, looks at paging.count
  * ~~Can get a listing of all the images~~ 04/13/2016: Gets count from step above, and gets all files
  * ~~Test paging~~ 04/14/2016: finds a file we uploaded.

2. Negative tests
  * ~~Invalid auth~~04/13/2016
  * ~~No auth provided~~ 04/13/2016
  * ~~Test paging out of bounds~~ 04/13/2016
  * ~~Test paging fuzzed parameters~~ 04/13/2016
  

3. Super evil tests
  * ~~Test paging bad type params~~ 04/14/2016
  * ~~Test paging HUGE / invalid numbers~~ 04/14/2016

####Files.delete 
1. Simple positive test
  * ~~Deletes first file uploaded in session~~ 04/14/2016
  
2. Negative tests
  * ~~Cannot delete same file again~~ 04/14/2016
  * ~~Delete something that never existed~~ 04/14/2016
  * ~~Delete without auth~~ 04/14/2016
  * ~~Delete with bad auth~~ 04/14/2016

3. Super evil tests
  * ~~Delete with HUGE fuzzed ID~~ 04/14/2016
  * ~~Delete with mildly fuzzed ID 1000~~ 04/14/2016

####Checksum postamble tests / unwinding:
* ~~Checksum: list all the files, ensure we have the files recorded~~ 04/14/2016
* ~~Checksum: delete all the files that we created, and have recorded~~ 04/14/2016
* Checksum: wait 30sec and start list again ensure nothing comes back.

##Findings:

#BUG #1 FOUND:
I have found throughout the course of this exercise that while performing some fuzzing tests with bad/inappropriate input, I may have caused the Slack API to have bad meta data with my account!
Here in this image: 

![alt text](https://raw.githubusercontent.com/cryptomail/public_problem_solving/master/slack/p1-be-qa/images/slack_bug.png "Black Box Slack API Testing")

We clearly see that the Slack API is professing that there are two files represented in the paging section of the results:
To me, paging.total = 2 means there will be two files, yet we see clearly from the URL that there are none.
I have a feeling that somehow the meta data got out of sync between the storage and the actual storage.
If this occurs, Slack API developers should be alerted and a careful analysis should be undertaken to understand why/how this could occur.
I understand that 'eventual consistency' may be at play, but as you can see here, the image you are seeing is after several hours at rest.


Both files.upload and files.delete appear to be asynchronous wherein the user may upload a file, yet it may or may not show in the listing at the time of request, so testing would require knowledge of that.  These tests are architected in such a way that they exercise a class of the API one at a time.  In this case: files.upload, followed by files.list, followed by files.delete.


#BUG #2 FOUND:
Sending a file to a channel referenced multiple times yields an iteration on the same channel when it should not.
Please see/analyze the following JavaScript:
```javascript
var fnameBase = 'kinderegg';
      var fname = fnameBase + '.png';
      var fnamePath = 'data' + '/' + fname;
      var formData = {
              channels: util.blabber('#general',1000,','),
              token:  secretToken ,
              file: {
                value:  fs.createReadStream(fnamePath),
                options: {
                  filename: fname,
                  contentType: 'image/png'
                }
              } 
            };
            
      
      util.issueSimplePOSTRequest('files','upload',formData)
```

Here we see the channels is blabbered/fuzzed with the same channel, resulting in the canonical output of 1000 messages to the same channel.

There are two problems that I will exemplify: 

1. The message delivery times out
2. It emits the message 1000 times to the same channel

This causes a test failure in my suite and it is my belief that the client is evil and should not be trusted and the message should NOT be emitted for as many times as there are the target channel.

The code is under the block: `it('Evil: Send to A LOT OF valid channels\nThis should pass in my opinion.')`

##Recommendation:
Do not iterate on the channels until they have been resolved to a set. aka pipe to uniq :fedora:

#Aynchronous Quirk FOUND:
It appears that some files take an inordinate amount of time to really disappear after asking for a formal delete.
It is possible to query the system and get a valid listing of a file that has been requested for deletion, for **almost a minute** in some circumstances.  The test library will at some points emit that it attempted to delete a file, that comes back from a listing, only to find out later, when trying to clean it up, it's previously tagged as deleted.
It is my opinion that after a file has been requested to be deleted, that it **never** show up in any results from a `files.list` query.

##Recommendation:
If a file has been requested for deletion,  ensure delete is synchronous,or fast enough to be perceived as such.

#The Very Good:
I have tested many fuzzy things like upload a PDF file, yet professing it's a PDF.
Please check: `it(Positive: unset filetype or LIED to DOT FILE resolves to correct filetype')`
When the listing query comes back from the file, the file is appropriately tagged with filetype PDF.  That means the system is adequately tesing for magic even if the file envelope is misleading.  
```javascript
var fnameBase = 'really_a_pdf';
      var fname = fnameBase + '.png';
      var fnamePath = 'data' + '/' + fname;
      var formData = {
              token:  secretToken ,
              file: {
                value:  fs.createReadStream(fnamePath),
                options: {
                  filename: fname,
                  contentType: 'image/png'
                }
              } 
            };
            util.issueSimplePOSTRequest('files','upload',formData)
```
Look at that bold face lie on `contentType` in the file, and look at the `fname` variables. All patent LIES and yet Slack API gets it right.
:thumbsup: zats what I like to see :heart:


  