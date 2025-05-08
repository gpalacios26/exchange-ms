package com.gpc.exchange.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("exchange")
public class Exchange {

  @Id
  private Long id;

  @Column("buy")
  private Double buy;

  @Column("sell")
  private Double sell;

  @Column("date_time")
  private LocalDateTime dateTime;
}
