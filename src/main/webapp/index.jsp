<html>
<body>
    <h2>Jersey RESTful Web Application!</h2>
    <p><a href="webapi/myresource">Jersey resource</a>
    <p>Visit <a href="http://jersey.java.net">Project Jersey website</a>
    for more information on Jersey!
    
     <button type="button" id="btnValidador" value="Validar axiomas" formmethod="POST"> Validar axioma </button>

        <script>
            document.getElementById("btnValidador").addEventListener("click", validarAxioma);
            
            function validarAxioma() {
                var xhttp = new XMLHttpRequest();
                xhttp.open("POST", "http://localhost:8080/owlapi/webapi/axioms/valida", true); 
                xhttp.setRequestHeader("Content-Type", "application/json");
                xhttp.onreadystatechange = function() {
                    if (this.readyState == 4 && this.status == 202) {
                        // Response
                        var response = this.responseText;
                        console.log(response);
                    }
                };
                var data = {
                            "classes": ["Pessoa", "Homem", "Mulher"],
                            "axiomas": ["Homem subClassOf (Pessoa)", "Mulher subClassOf (Pessoa)"],
                            "propriedades": ["hasPart"]
                        };
                xhttp.send(JSON.stringify(data));
            }
        </script>
</body>
</html>
