Feature: Robust handling of invalid Prometheus metrics
  As a user
  I want the system to ignore invalid or incomplete metric records
  So that bad monitoring data does not cause unnecessary timeout changes

  Background:
    Given the acceptable error rate is 2 percent
    And the maximum timeout is 1500 ms
    And the minimum change window is 2 minutes
    And the timeout step size is 100 ms
    And there is a route "route-1" with initial timeout 1000 ms

  Scenario Outline: Ignore invalid metric records for a route
    Given an incoming Prometheus metric with routeId "<routeId>" and value kind "<valueKind>"
    When the scheduler checks Prometheus metrics
    Then no timeout change should be performed for "route-1"
    And the stored timeout for "route-1" should remain 1000 ms

    Examples:
      | routeId   | valueKind      |
      | null      | too_short      |  # value.size < 2 и отсутствует routeId
      | route-1   | too_short      |  # value.size < 2
      | route-1   | not_a_number   |  # invalid numeric value
      | unknown   | normal         |  # нет таймаута для данного routeId
