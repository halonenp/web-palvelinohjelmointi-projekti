/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author halon
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends AbstractPersistable<Long> {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeposted = LocalDateTime.now();
    private String content; //the message
    private String writer; //account's username which the message is from
    private int likes = 0;
    private String likers;

    @OneToMany(mappedBy = "chat")
    private List<Comment> comments;

}
