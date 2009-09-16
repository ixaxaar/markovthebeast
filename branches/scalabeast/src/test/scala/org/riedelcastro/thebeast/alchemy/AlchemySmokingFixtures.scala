package org.riedelcastro.thebeast.alchemy

/**
 * @author Sebastian Riedel
 */

trait AlchemySmokingFixtures {
  val smokingSignature = """
    // Evidence
    Friends(person, person)

    // Some evidence, some query
    Smokes(person)

    // Query
    Cancer(person)
  """

  val smokingRules = """
    // Rules
    // If you smoke, you get cancer
    Smokes(x) => Cancer(x)

    // People with friends who smoke, also smoke
    // and those with friends who don't smoke, don't smoke
    Friends(x, y) => (Smokes(x) <=> Smokes(y))
  """
}