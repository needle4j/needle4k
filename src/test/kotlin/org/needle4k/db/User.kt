package org.needle4k.db

import javax.persistence.*

@Entity
@Table(name = User.TABLE_NAME)
class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  open val id: Long = 0

  companion object {
    const val TABLE_NAME = "NEEDLE_TEST_USER"
  }
}