package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Modelos.DAO.InterfaceUsuarioDAO;
import com.example.demo.Modelos.Entity.Usuario;

//Guardia de seguridad de la puerta
@Service("JpaServicioDetalleUsuario")
public class JpaServicioDetalleUsuario implements UserDetailsService{

    @Autowired
    private InterfaceUsuarioDAO usuarioDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByUsuario(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        
        List<GrantedAuthority> autoridades = new ArrayList<>();
        String rol = usuario.getRol();
        if (rol != null && !rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol;
        }
        autoridades.add(new SimpleGrantedAuthority(rol));
        return new User(usuario.getUsuario(), usuario.getContraseña(), true, true, true, true, autoridades);
    }
    
}
