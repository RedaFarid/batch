
package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String identifier = "";
    private String source = "";
    private String Event = "";
    @CreatedDate
    private LocalTime time;
    @CreatedDate
    private LocalDate Date;

    public Log(String identifier, String source, String event) {
        this.identifier = identifier;
        this.source = source;
        Event = event;
    }

    public Log(String identifier, String event) {
        this.identifier = identifier;
        Event = event;
    }
}
