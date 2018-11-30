<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script src="//blueimp.github.io/jQuery-File-Upload/js/vendor/jquery.ui.widget.js"></script>
        <script src="//blueimp.github.io/JavaScript-Load-Image/js/load-image.all.min.js"></script>
        <script src="//blueimp.github.io/JavaScript-Canvas-to-Blob/js/canvas-to-blob.min.js"></script>
        <script src="//blueimp.github.io/jQuery-File-Upload/js/jquery.iframe-transport.js"></script>
        <script src="//blueimp.github.io/jQuery-File-Upload/js/jquery.fileupload.js"></script>
        <script src="//blueimp.github.io/jQuery-File-Upload/js/jquery.fileupload-process.js"></script>
        <script src="//blueimp.github.io/jQuery-File-Upload/js/jquery.fileupload-image.js"></script>

        <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
        <style type="text/css">
            .dropzone { border: 1px dashed #cccccc; padding: 30px; background: #fff; }
            .dropzone .fileupload_label #fileupload { display: none; }
            form, #progress, input, .form-actions, #files { margin: 10px 0; }
            .thumbnail { margin: 10px 10px 0 0; }
        </style>
        <script>
        var filesList = new Array();
        $(function () {
            $('#fileupload').fileupload({
                autoUpload: false,
                dropZone: $('#dropzone')
            }).on('fileuploadadd', function (e, data) {
                data.context = $('<div/>', { class: 'thumbnail pull-left' }).appendTo('#files');
                $.each(data.files, function (index, file) {
                    filesList.push(data.files[index]);
                    console.log("Added: " + data.files[index].name);
                    var node = $('<p/>').append($('<span/>').text(file.name).data(data));
                    node.appendTo(data.context);
                });
            }).on('fileuploadprocessalways', function (e, data) {
                var index = data.index,
                    file = data.files[index],
                    node = $(data.context.children()[index]);
                if (file.preview) {
                    node.prepend('<br>').prepend(file.preview);
                }
                if (file.error) {
                    node.append('<br>').append($('<span class="text-danger"/>').text(file.error));
                }
            }).prop('disabled', !$.support.fileInput)
                .parent().addClass($.support.fileInput ? undefined : 'disabled');

            $("#uploadform").submit(function(event) {
                if (filesList.length > 0) {
                    console.log("multi file submit");
                    event.preventDefault();
                    $('#fileupload').fileupload('send', {files: filesList})
                        .success(function (result, textStatus, jqXHR) { console.log('success'); })
                        .error(function (jqXHR, textStatus, errorThrown) { console.log('error'); })
                        .complete(function (result, textStatus, jqXHR) { 
                            console.log('complete: ' + JSON.stringify(result));
                            // window.location='back to view-page after submit?'
                        });
                } else {
                    console.log("plain default form submit");
                }
            });
        });
        </script>

    </head>
    <body>

        <div class="container">
            <form id="uploadform" action="${pageContext.request.contextPath}/media/upload" method="POST" enctype="multipart/form-data">
                <h1>File upload test</h1>
                <p>

                    Here is a working example on how to get multi file upload with both dnd and selection
                    through file browser working with the jquery file upload, with no ajax upload. The
                    files will be uploaded when the form itself is submitted using a normal submit button.

                    The trick is to turn off auto upload, then collect the files being added in an array,
                    and the intercept the form submit. Whenever there are files to be uploaded, a submit
                    is done (with the complete form and files using multipart encoding) with a single ajax call, 
                    with a redirect after the call succeeds.
                </p>


                <input type="text" name="dummy" placeholder="some other input field">

                <div id="dropzone" class="dropzone">
                    <div class="fileupload_wrapper">
                        Dropp files here to upload, or
                        <label class="fileupload_label">browse...
                            <input id="fileupload" type="file" name="files" multiple="multiple">
                        </label>
                        (multiple files can be selected)
                    </div>
                </div>

                <div id="files" class="thumbnails clearfix"></div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">submit form</button>
                </div>
            </form>
        </div>
    </body>
</html>