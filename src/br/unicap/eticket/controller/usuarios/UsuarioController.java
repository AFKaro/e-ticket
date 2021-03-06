package br.unicap.eticket.controller.usuarios;

import br.unicap.eticket.dao.UsuarioDAO;
import br.unicap.eticket.model.usuarios.Usuario;
import java.util.List;
import br.unicap.eticket.excecoes.DadosInvalidosException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class UsuarioController {

    private UsuarioDAO dao;

    public UsuarioController() {
        this.dao = new UsuarioDAO();
    }

    /**
     * Busca um usuario pelo e-mail, seja ele um Cliente ou Admin
     *
     * @param user
     * @return
     */
    public Usuario buscarUser(Usuario user) {
        return dao.buscarUser(user.getEmail());
    }

    /**
     * Usuario (Cliente ou Admin) pode fazer login se suas informações estiverem
     * corretas
     *
     * @param email
     * @param senha
     * @return
     * @throws DadosInvalidosException
     */
    public Usuario login(String email, String senha) throws DadosInvalidosException {
        ExecutorService es = Executors.newCachedThreadPool();
        try {
            List<Usuario> aux = es.submit(() -> dao.consultar("adminLogin", "email", email, "senha", senha)).get();
            List<Usuario> aux2 = es.submit(() -> dao.consultar("clienteLogin", "email", email, "senha", senha)).get();

            aux.addAll(aux2);
            if (aux.isEmpty()) {
                throw new DadosInvalidosException("Login");
            } else {
                return aux.get(0);
            }

        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
