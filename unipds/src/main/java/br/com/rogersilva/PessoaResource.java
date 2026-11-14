package br.com.rogersilva;

import io.micrometer.core.annotation.Counted;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.random.RandomGenerator;

@Path("/pessoa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PessoaResource {
    DataUtils dataUtils = new DataUtils();

    @GET
    @Counted(value = "counted.getPessoa")
    public List<Pessoa> getPessoa() {
        return Pessoa.listAll();
    }

    @POST
    @Transactional
    public Pessoa addPessoa(Pessoa pessoa) {
        pessoa.id = null;
        pessoa.persist();
        return pessoa;
    }

    @PUT
    @Transactional
    public Pessoa updatePessoa(@QueryParam("id") Long id) {
        Pessoa pessoa = Pessoa.findById(id);
        pessoa.nome = dataUtils.geraNome();
        pessoa.anoNascimento = RandomGenerator.getDefault().nextInt((2026 - 1900) + 1) + 1900;
        pessoa.persist();
        return pessoa;
    }

    @GET
    @Path("/anoNascimento")
    public List<Pessoa> findByAnoNascimento(@QueryParam("anoNascimento") int anoNascimento) {
        return Pessoa.findByAnoNascimento(anoNascimento);
    }

    @DELETE
    @Transactional
    public void removePessoa(@QueryParam("id") Long id) {
        Pessoa pessoa = Pessoa.findById(id);
        pessoa.delete();
    }

    @POST
    @Transactional
    @Path("/geraPessoas")
    public List<Pessoa> geraPessoas() {
        for(int i = 0; i <=100; i++){
            Pessoa pessoa = new Pessoa();
            pessoa.nome = dataUtils.geraNome();
            pessoa.anoNascimento = RandomGenerator.getDefault().nextInt((2026 - 1900) + 1) + 1900;
            pessoa.persist();
        }
        return Pessoa.listAll();
    }
}
