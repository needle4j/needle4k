package org.needle4k.db

import javax.persistence.*

@Entity(name = "personEntity")
@Table(name = Person.TABLE_NAME)
class Person {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  open val id: Long = 0

  @Column(nullable = false)
  lateinit var myName: String

  @OneToOne(cascade = [CascadeType.ALL], mappedBy = "person")
  lateinit var address: Address

  companion object {
    const val TABLE_NAME = "NEEDLE_TEST_PERSON"
  }
}