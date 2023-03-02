package org.needle4k.db

import javax.persistence.*

@Entity
@Table(name = Address.TABLE_NAME, uniqueConstraints = [UniqueConstraint(columnNames = ["id", "zip"])])
open class Address {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  open val id: Long = 0

  @OneToOne
  lateinit var person: Person
  var street: String = ""

  var zip: String = ""

  companion object {
    const val TABLE_NAME = "NEEDLE_TEST_ADDRESS"
  }
}