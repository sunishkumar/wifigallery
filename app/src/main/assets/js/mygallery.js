


       function download(type) {


            var ids = [];
            var names = [];

            $.each($("input[name='resource']:checked"), function(){

                ids.push($(this).val());
                names.push($(this).attr("display-name"));
                $(this).prop( "checked", false );
            });

            Android.downloadFiles( type,ids,names);




        }

        function viewImage(id) {

            var ids =[];

            $.each($("input[name='resource']"), function(){

                ids.push($(this).val());

            });

            Android.viewImage(id, ids.join(","));



        }

         function viewVideo(id) {

                    var ids =[];

                    $.each($("input[name='resource']"), function(){

                        ids.push($(this).val());

                    });

                    Android.viewVideo(id, ids.join(","));



                }