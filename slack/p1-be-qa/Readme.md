#Slack API Testing


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
1. Field limits testing

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

2. Negative tests
  * ~~Invalid auth~~04/13/2016
  * ~~No auth provided~~ 04/13/2016
3. Super evil tests

####Files.delete 
1. Simple positive test
  * ~~Deletes first file uploaded in session~~ 04/13/2016


2. Negative tests
  * ~~Cannot delete same file again~~ 04/13/2016
3. Super evil tests